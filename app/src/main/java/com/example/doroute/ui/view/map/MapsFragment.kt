package com.example.doroute.ui.view.map

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.doroute.R
import com.example.doroute.data.database.RoomDatabase
import com.example.doroute.data.domain.stores.TaskDbStore
import com.example.doroute.data.models.ClusterMarker
import com.example.doroute.data.models.PolylineData
import com.example.doroute.data.models.TaskModel
import com.example.doroute.helpers.ClusterManagerRenderer
import com.example.doroute.helpers.TaskStates
import com.example.doroute.ui.viewmodel.TaskViewModel
import com.example.doroute.ui.viewmodel.TaskViewModelFactory
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnPolylineClickListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.DirectionsResult
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import java.util.*
import java.util.UUID.randomUUID


class MapsFragment : Fragment(),
    OnMapReadyCallback,
    OnInfoWindowClickListener,
    OnPolylineClickListener {

    private lateinit var rootView: View

    //Map
    private var mMap: GoogleMap? = null
    private var mapView: MapView? = null
    private var mGeoApiContext: GeoApiContext? = null //for DirectionsApi
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null

    //Marker
    private var mTripMarkers = ArrayList<Marker>()
    private var mClusterManager: ClusterManager<ClusterMarker>? = null
    private var mClusterMarkers: ArrayList<ClusterMarker>? = null
    private var mClusterManagerRenderer: ClusterManagerRenderer? = null
    private var mSelectedMarker: Marker? = null

    //Model
    private var mLastKnownLocation: Location? = null //device's last known location
    private var mTasks: List<TaskModel>? = null
    private lateinit var taskViewModel: TaskViewModel

    //Places
    private var predictionList: List<AutocompletePrediction>? = null
    private lateinit var placesClient: PlacesClient

    //Polyline
    private var mPolyLinesData = ArrayList<PolylineData>()

    //widgets
    private lateinit var materialSearchBar: MaterialSearchBar

    //Lifecycle
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_maps, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = rootView.findViewById(R.id.map)
        mapView!!.onCreate(savedInstanceState)
        mapView!!.onResume()
        mapView!!.getMapAsync(this) //therefore, onMapReady runs after onViewCreated
        Places.initialize(requireContext(), getString(R.string.mapsKey))
        placesClient = Places.createClient(this.requireContext())

        if (mGeoApiContext == null) {
            mGeoApiContext = GeoApiContext.Builder()
                .apiKey(getString(R.string.mapsKey))
                .build()
        }

        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this.requireContext())
        val taskFactory =
            TaskViewModelFactory(
                TaskDbStore(
                    RoomDatabase.getDb(
                        requireContext()
                    )
                )
            )

        //attach the viewmodel to the activity, not the fragment, to prevent memory leak
        taskViewModel = requireActivity().let {
            ViewModelProvider(
                this,
                taskFactory
            ).get(TaskViewModel::class.java)
        }

        initSearchBar()
    }

    private fun addTask() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation()
            }
        }
    }

    //Map
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.isMyLocationEnabled = true
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
        mMap!!.setOnMapLongClickListener(this::onMapLongClick)

        val activity = requireActivity()

        mClusterManager = ClusterManager<ClusterMarker>(activity, mMap)
        mMap!!.setOnPolylineClickListener(this)
        mClusterManager = ClusterManager<ClusterMarker>(activity, mMap)
        mClusterManagerRenderer = ClusterManagerRenderer(activity, mMap, mClusterManager)
        mClusterManager?.renderer = mClusterManagerRenderer

        findMyLocationButton()
        locationSettingRequest(activity)

        taskViewModel.tasksLiveData.observe(viewLifecycleOwner, Observer {
            mTasks = it
            addMapMarkers()
        })

        mMap!!.setOnMyLocationButtonClickListener {
            if (materialSearchBar.isSuggestionsVisible) materialSearchBar.clearSuggestions()
            if (materialSearchBar.isSearchEnabled) materialSearchBar.disableSearch()
            false
        }
    }

    private fun onMapLongClick(latLng: LatLng) {
        val taskModel = TaskModel(
            randomUUID().toString(),
            randomUUID().toString(),
            "ceva",
            "da",
            Calendar.getInstance().time,
            latLng.latitude,
            latLng.longitude,
            "altundeva",
            TaskStates.OVERDUE,
            false
        )
        taskViewModel.addTask(taskModel,requireContext())
    }

    private fun locationSettingRequest(activity: FragmentActivity) {
        //check if gps is enabled or not and then request user to enable it
        val locationRequest: LocationRequest = getLocationRequest()

        val builder: LocationSettingsRequest.Builder =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient: SettingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener(requireActivity()) { getDeviceLocation() }
            .addOnFailureListener(activity) { e ->
                if (e is ResolvableApiException) {
                    val resolvable: ResolvableApiException = e
                    try {
                        resolvable.startResolutionForResult(activity, 51)
                    } catch (e1: IntentSender.SendIntentException) {
                        e1.printStackTrace()
                    }
                }
            }
    }

    private fun getLocationRequest(): LocationRequest {
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return locationRequest
    }

    private fun resetMap() {
        mMap?.clear()
        if (mClusterManager != null) {
            mClusterManager?.clearItems()
            if (mClusterMarkers != null && mClusterMarkers?.size!! > 0) {
                mClusterMarkers?.clear()
                mClusterMarkers = ArrayList<ClusterMarker>()
            }
        }
        if (mPolyLinesData.size > 0) {
            mPolyLinesData.clear()
            mPolyLinesData = ArrayList()
        }
    }

    private fun getDeviceLocation() {
        mFusedLocationProviderClient!!.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                mLastKnownLocation = task.result
                if (mLastKnownLocation != null) {
                    mMap!!.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                mLastKnownLocation!!.latitude,
                                mLastKnownLocation!!.longitude
                            ), DEFAULT_ZOOM
                        )
                    )
                } else {
                    computeLatestLocation()
                }
            } else {
                Toast.makeText(activity, "unable to get last location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun computeLatestLocation() {
        //compute a new lastLocation
        //start by building the required parameter objects
        val locationRequest = getLocationRequest()
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                if (locationResult == null) {
                    return
                }
                mLastKnownLocation = locationResult.lastLocation
                if (mLastKnownLocation != null) {
                    mMap!!.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                mLastKnownLocation!!.latitude,
                                mLastKnownLocation!!.longitude
                            ), DEFAULT_ZOOM
                        )
                    )
                    mFusedLocationProviderClient!!.removeLocationUpdates(this) //Once I get the location, there's no need for other location updates
                }
            }
        }

        mFusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    private fun setCameraView() {

        // Set a boundary to start
        val bottomBoundary: Double = mTasks!!.last().latitude - .01
        val leftBoundary: Double = mTasks!!.last().longitude - .01
        val topBoundary: Double = mTasks!!.last().latitude + .01
        val rightBoundary: Double = mTasks!!.last().longitude + .01
        val mMapBoundary = LatLngBounds(
            LatLng(bottomBoundary, leftBoundary),
            LatLng(topBoundary, rightBoundary)
        )
        mMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0))
    }

    private fun zoomRoute(lastLatLngRoute: List<LatLng>?) {
        if (mMap == null || lastLatLngRoute == null || lastLatLngRoute.isEmpty()) return
        val boundsBuilder = LatLngBounds.Builder()
        lastLatLngRoute.forEach {
            boundsBuilder.include(it)
        }
        val routePadding = 50
        val latLngBounds = boundsBuilder.build()
        mMap!!.animateCamera(
            CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
            600,
            null
        )
    }


    //Markers

    private fun addMapMarkers() {
        //resetMap()
        mMap!!.setOnInfoWindowClickListener(this)
        mTasks?.forEach { task ->
            Log.d(TAG, "addMapMarkers: location: " + task.locationName)
            val snippet = "Determine route to this task?"
            val avatar: Int = R.drawable.map_marker // set the default avatar
            val newClusterMarker = ClusterMarker(
                LatLng(task.latitude, task.longitude),
                task.title,
                snippet,
                avatar,
                task
            )
            mClusterManager?.addItem(newClusterMarker)
            mClusterMarkers?.add(newClusterMarker)

        }
        mClusterManager?.cluster() //make the new cluster visible
        setCameraView() //and focus on it
    }


    private fun calculateDirections(marker: Marker) {
        val directions = DirectionsApiRequest(mGeoApiContext)
        if (mLastKnownLocation == null)
            getDeviceLocation() // it's not async. This method will not return until i have a location

        directions.alternatives(true)
        directions.destination( //task destination
            com.google.maps.model.LatLng(
                marker.position.latitude,
                marker.position.longitude
            )
        )
        directions.origin(//my location
            com.google.maps.model.LatLng(
                mLastKnownLocation!!.latitude,
                mLastKnownLocation!!.longitude
            )
        )
        directions.setCallback(object : PendingResult.Callback<DirectionsResult?> {
            override fun onResult(result: DirectionsResult?) {
                if (result != null) {
//                    Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString())
//                    Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration)
//                    Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance)
//                    Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString())
                    addPolylinesToMap(result)
                }
            }

            override fun onFailure(e: Throwable) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.message)
            }
        })

    }
/*
    private fun resetSelectedMarker() {
        if (mSelectedMarker != null) {
            mSelectedMarker!!.isVisible = true
            mSelectedMarker = null
            removeTripMarkers()
        }
    }

    private fun removeTripMarkers() {
        mTripMarkers.forEach { marker ->
            marker.remove()
        }
    }

 */

    override fun onInfoWindowClick(marker: Marker) {
        AlertDialog.Builder(activity)
            .setMessage(marker.snippet)
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog, id ->
                //resetSelectedMarker()
                mSelectedMarker = marker
                calculateDirections(marker)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, id -> dialog.cancel() }
            .create()
            .show()
    }

    //Polylines

    //for the selected polyline, show its duration change its color and add a marker with a custom text
    override fun onPolylineClick(polyline: Polyline) {
        mPolyLinesData.forEach { polylineData ->
            if (polyline.id == polylineData.polyline.id) {
                polylineData.polyline.color =
                    ContextCompat.getColor(requireContext(), R.color.quantum_bluegrey400)
                polylineData.polyline.zIndex = 1.0F
                val endLocation =
                    LatLng(
                        polylineData.leg.endLocation.lat,
                        polylineData.leg.endLocation.lng
                    )
                val marker: Marker = mMap!!.addMarker(
                    MarkerOptions()
                        .position(endLocation)
                        .title("Trip #${mPolyLinesData.indexOf(polylineData)}")
                        .snippet(
                            "Duration: " + polylineData.leg.duration
                        )
                )
                mTripMarkers.add(marker)
                marker.showInfoWindow()
            } else {
                polylineData.polyline.color =
                    ContextCompat.getColor(requireContext(), R.color.quantum_bluegrey500)
                polylineData.polyline.zIndex = 0F
            }
        }
    }

    private fun addPolylinesToMap(result: DirectionsResult) {
        Handler(Looper.getMainLooper()).post {
            Log.d(TAG, "run: result routes: " + result.routes.size)
            if (mPolyLinesData.isNotEmpty()) {
                mPolyLinesData.forEach {
                    it.polyline.remove() //safe delete
                }
                mPolyLinesData.clear()
            }
            var duration = 999999999.0
            result.routes.forEach { route ->
                val decodedPath =
                    PolylineEncoding.decode(route.overviewPolyline.encodedPath)
                val newDecodedPath: MutableList<LatLng> =
                    ArrayList()

                // This loops through all the LatLng coordinates of ONE polyline.
                decodedPath.forEach { latLng ->
                    //Log.d(TAG, "run: latlng: " + latLng.toString());
                    newDecodedPath.add(
                        LatLng(
                            latLng.lat,
                            latLng.lng
                        )
                    )
                }
                val polyline: Polyline =
                    mMap!!.addPolyline(PolylineOptions().addAll(newDecodedPath))

                //default color
                polyline.color = ContextCompat.getColor(requireActivity(), R.color.quantum_grey)
                polyline.isClickable = true
                mPolyLinesData.add(PolylineData(polyline, route.legs[0]))

                // highlight the fastest route and adjust camera
                val tempDuration =
                    route.legs[0].duration.inSeconds.toDouble()
                if (tempDuration < duration) {
                    duration = tempDuration
                    onPolylineClick(polyline) //simulates the user click
                    zoomRoute(polyline.points)
                }
                mSelectedMarker!!.isVisible = false
            }
        }
    }

    //Widgets
    private fun initSearchBar() {
        materialSearchBar = rootView.findViewById(R.id.searchBar)
        materialSearchBar.setOnSearchActionListener(object : OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {}
            override fun onSearchConfirmed(text: CharSequence) {
                requireActivity().startSearch(text.toString(), true, null, true)
            }

            override fun onButtonClicked(buttonCode: Int) {
                when (buttonCode) {
                    MaterialSearchBar.BUTTON_BACK ->
                        materialSearchBar.disableSearch()
                }
            }
        })

        val token = AutocompleteSessionToken.newInstance()
        materialSearchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val predictionsRequest =
                    FindAutocompletePredictionsRequest.builder()
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build()

                placesClient.findAutocompletePredictions(predictionsRequest)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val predictionsResponse: FindAutocompletePredictionsResponse? =
                                task.result
                            if (predictionsResponse != null) {
                                predictionList =
                                    predictionsResponse.autocompletePredictions
                                val suggestionsList: List<String>?
                                suggestionsList = predictionList?.map { prediction ->
                                    prediction.getFullText(null).toString()
                                }

                                materialSearchBar.updateLastSuggestions(suggestionsList)
                                if (!materialSearchBar.isSuggestionsVisible) {
                                    materialSearchBar.showSuggestionsList()
                                }
                            }
                        } else {
                            Log.i(TAG, "prediction fetching task unsuccessful")
                        }
                    }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        materialSearchBar.setSuggstionsClickListener(object :
            SuggestionsAdapter.OnItemViewClickListener {
            override fun OnItemClickListener(position: Int, v: View?) {
                if (position >= predictionList!!.size) {
                    return
                }
                val selectedPrediction = predictionList!![position]
                val suggestion =
                    materialSearchBar.lastSuggestions[position].toString()
                materialSearchBar.text = suggestion
                Handler().postDelayed(Runnable { materialSearchBar.clearSuggestions() }, 1000)

                val imm: InputMethodManager? =
                    requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?

                imm?.hideSoftInputFromWindow(
                    materialSearchBar.windowToken,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )

                val placeId = selectedPrediction.placeId
                val placeFields: List<Place.Field> = listOf(Place.Field.LAT_LNG, Place.Field.NAME)
                val fetchPlaceRequest =
                    FetchPlaceRequest.builder(placeId, placeFields).build()
                placesClient.fetchPlace(fetchPlaceRequest)
                    .addOnSuccessListener { fetchPlaceResponse ->
                        val place = fetchPlaceResponse?.place
                        Log.i("mytag", "Place found: " + place?.name)
                        val latLngOfPlace =
                            place?.latLng
                        if (latLngOfPlace != null) {
                            mMap!!.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    latLngOfPlace,
                                    DEFAULT_ZOOM
                                )
                            )
                        }
                    }.addOnFailureListener { e ->
                        if (e is ApiException) {
                            e.printStackTrace()
                            val statusCode = e.statusCode
                            Log.i(TAG, "place not found: " + e.message)
                            Log.i(TAG, "status code: $statusCode")
                        }
                    }
            }

            override fun OnItemDeleteListener(position: Int, v: View?) {}
        })
    }

    private fun findMyLocationButton() {
        val locationButton =
            (mapView!!.findViewById<View>("1".toInt())
                .parent as View).findViewById<View>("2".toInt())
        val layoutParams: RelativeLayout.LayoutParams =
            locationButton.layoutParams as RelativeLayout.LayoutParams
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        layoutParams.setMargins(0, 0, 40, 180)
    }


    companion object {
        private const val TAG = "MapFragment"
        private const val DEFAULT_ZOOM = 15f
    }
}
