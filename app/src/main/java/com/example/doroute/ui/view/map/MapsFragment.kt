package com.example.doroute.ui.view.map

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
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
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.doroute.R
import com.example.doroute.data.database.RoomDatabase
import com.example.doroute.data.domain.TaskDbStore
import com.example.doroute.data.models.PolylineData
import com.example.doroute.data.models.TaskModel
import com.example.doroute.helpers.LocationHelper
import com.example.doroute.helpers.TaskStates
import com.example.doroute.ui.viewmodel.TaskViewModel
import com.example.doroute.ui.viewmodel.TaskViewModelFactory
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnPolylineClickListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.DirectionsResult
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import kotlinx.android.synthetic.main.create_task_wizard.view.*
import java.util.*
import java.util.UUID.randomUUID
import kotlin.collections.ArrayList


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
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        mapView = rootView.findViewById(R.id.map)
        mapView!!.onCreate(savedInstanceState)
        mapView!!.onResume()
        mapView!!.getMapAsync(this) //therefore, onMapReady runs after onViewCreated
        Places.initialize(requireActivity(), getString(R.string.mapsKey))
        placesClient = Places.createClient(this.requireActivity())

        if (mGeoApiContext == null) {
            mGeoApiContext = GeoApiContext.Builder()
                .apiKey(getString(R.string.mapsKey))
                .build()
        }

        val taskFactory =
            TaskViewModelFactory(
                TaskDbStore(
                    RoomDatabase.getDb(
                        requireContext()
                    )
                )
            )

        taskViewModel = requireActivity().run {
            ViewModelProvider(
                this,
                taskFactory
            ).get(TaskViewModel::class.java)
        }
        initSearchBar()
    }

    private fun syncMap() {
        mMap?.clear()
        removePolylinesData()
        taskViewModel.retrieveTasks()
        mTasks?.forEach {task ->
            if (task.status != TaskStates.COMPLETE) {
                addMapMarkerForTask(task)
                if (task.tripActive)
                    loadPolylinesFromTasks(task)
            }
        }
    }

    private fun addTask(task: TaskModel){
        taskViewModel.addTask(task)
        addMapMarkerForTask(task)
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
        syncMap()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        syncMap()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
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
        val activity = requireActivity()

        mMap = googleMap
        mMap!!.isMyLocationEnabled = true
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
        mMap!!.setOnMapLongClickListener(this::onMapLongClick)
        mMap!!.setOnPolylineClickListener(this)
        LocationHelper.findMyLocationButton(mapView!!)
        LocationHelper.locationSettingRequest(activity, this::getDeviceLocation)

        //Observers
        taskViewModel.tasksLiveData.observe(viewLifecycleOwner, Observer {
            mTasks = it
        })

        //Widgets
        mMap!!.setOnMyLocationButtonClickListener {
            syncMap()
            if (materialSearchBar.isSuggestionsVisible) materialSearchBar.clearSuggestions()
            if (materialSearchBar.isSearchEnabled) materialSearchBar.disableSearch()
            false
        }
        syncMap()
    }

    private fun onMapLongClick(latLng: LatLng) {
        val now = Calendar.getInstance(Locale.getDefault())
        val builder = AlertDialog.Builder(activity)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.create_task_wizard, null)
//        Populate a random field just to see whether it works
        val dueDateField = view.findViewById(R.id.wizard_task_dueDate) as EditText
        var dueDate = now.time
        dueDateField.setOnClickListener {
            val selectedCalendar = Calendar.getInstance(Locale.getDefault()) // variable to collect custom date and time
            val datePicker = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    //since the listener runs only after the user finished to pick a date...
                    selectedCalendar.set(Calendar.YEAR, year)
                    selectedCalendar.set(Calendar.MONTH, month)
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val timePicker = TimePickerDialog(
                        context,
                        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                            //... I can simulate an async behavior by using only the flow logic...
                            selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            selectedCalendar.set(Calendar.MINUTE, minute)
                            selectedCalendar.set(Calendar.SECOND, 0)
                            selectedCalendar.set(Calendar.MILLISECOND, 0)
                            dueDateField.setText(selectedCalendar.time.toString())
                            dueDate = selectedCalendar.time
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false
                    )
                    //...and without spoiling the UX
                    timePicker.show()
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
        builder.setTitle("Create Task")
            .setCancelable(false)
            .setIcon(R.drawable.ic_schedule)
            .setView(view)
            .setCancelable(true)
            .setPositiveButton("Submit") { _, _ ->
                val title = view.wizard_task_title.text.toString()
                val description = view.wizard_task_description.text.toString()
                val task = TaskModel(
                    randomUUID().toString(),
                    title,
                    description,
                    dueDate,
                    latLng,
                    TaskStates.PENDING,
                    false,
                    false
                )
                addTask(task)
            }
        builder.create()
        builder.show()
    }



    private fun loadPolylinesFromTasks(task: TaskModel) {
        if (mLastKnownLocation != null) {
            val origin = com.google.maps.model.LatLng(
                mLastKnownLocation!!.latitude, mLastKnownLocation!!.longitude
            )
            val destination: com.google.maps.model.LatLng =
                com.google.maps.model.LatLng(task.location.latitude, task.location.longitude)
            calculateDirections(origin, destination)
        }
    }


    //Markers
    private fun addMapMarkerForTask(task: TaskModel) {
        mMap!!.setOnInfoWindowClickListener(this)
        val avatar: Int = R.drawable.map_marker
        val time = task.dueDate.toString().substringBefore("GMT")
        val markerOptions = MarkerOptions()
            .position(LatLng(task.location.latitude, task.location.longitude))
            .icon(BitmapDescriptorFactory.fromResource(avatar))
            .title(task.title)
            .snippet(time)
        val marker = mMap!!.addMarker(markerOptions)
        marker.showInfoWindow()
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
                    computeLatestLocation() //requests for a new location
                }
            } else {
                Toast.makeText(activity, "unable to get last location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun computeLatestLocation() {
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
        val locationRequest = LocationHelper.getLocationRequest()
        mFusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }



    private fun calculateDirections(
        origin: com.google.maps.model.LatLng,
        destination: com.google.maps.model.LatLng
    ) {
        val directions = DirectionsApiRequest(mGeoApiContext)
        directions.alternatives(true)
        directions.destination(destination)
        directions.origin(origin)
        directions.setCallback(object : PendingResult.Callback<DirectionsResult?> {
            override fun onResult(result: DirectionsResult?) {
                if (result != null) {
                    addPolylinesToMap(result)
                }
            }
            override fun onFailure(e: Throwable) {}
        })
    }

    override fun onInfoWindowClick(marker: Marker) {
        //marks a task's tripActive with true and creates polylines for the task whose location is equal to marker's
        AlertDialog.Builder(activity)
            .setMessage("Do you want to start a trip to this task?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog, _ ->

                val task = taskViewModel.getTaskByLocation(marker.position)
                if (!task.tripActive) {
                    task.tripActive = true
                    taskViewModel.updateTask(task)
                    loadPolylinesFromTasks(task)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "The trip is already active",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    //Polylines

    //for the selected polyline, show its duration change its color and add a marker with a custom text
    override fun onPolylineClick(polyline: Polyline) {
        mPolyLinesData.forEach { polylineData ->
            //get the task at the destination
            val endLocation =
                LatLng(
                    polylineData.leg.endLocation.lat,
                    polylineData.leg.endLocation.lng
                )
            //val task = taskViewModel.getTaskByLocation(endLocation)
            if (polyline.id == polylineData.polyline.id) {
                polylineData.polyline.color =
                    ContextCompat.getColor(requireContext(), R.color.colorPolylineSelected)
                polylineData.polyline.zIndex = 1.0F
                val marker: Marker = mMap!!.addMarker(
                    MarkerOptions()
                        .position(endLocation)
                        .title("Trip #${mPolyLinesData.indexOf(polylineData)}")
                        .snippet("Duration: " + polylineData.leg.duration)
                )

                marker.showInfoWindow()
            } else {
                polylineData.polyline.color =
                    ContextCompat.getColor(requireContext(), R.color.colorPolylineNotSelected)
                polylineData.polyline.zIndex = 0F
            }
        }
    }


    private fun addPolylinesToMap(result: DirectionsResult) {
        Handler(Looper.getMainLooper()).post {
            Log.d(TAG, "run: result routes: " + result.routes.size)

            result.routes.forEach { route ->
                val decodedPath = PolylineEncoding.decode(route.overviewPolyline.encodedPath)
                val newDecodedPath: MutableList<LatLng> = ArrayList()

                // This loops through all the LatLng coordinates of ONE polyline.
                decodedPath.forEach { latLng ->
                    newDecodedPath.add(
                        LatLng(latLng.lat, latLng.lng)
                    )
                }
                val polyline: Polyline =
                    mMap!!.addPolyline(PolylineOptions().addAll(newDecodedPath))

                //default color
                polyline.color = ContextCompat.getColor(requireContext(), R.color.quantum_grey)
                polyline.isClickable = true
                mPolyLinesData.add(PolylineData(polyline, route.legs[0]))
            }
        }
    }

    private fun removePolylinesData() {
        if (mPolyLinesData.isNotEmpty()) {
            mPolyLinesData.forEach {polyLineData ->
                polyLineData.polyline.remove() //safe delete
            }
            mPolyLinesData.clear()
        }
    }

    //Widgets
    private fun initSearchBar() {
        materialSearchBar = rootView.findViewById(R.id.searchBar)
        materialSearchBar.setNavButtonEnabled(false) //Hide navigation button
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
                Handler().postDelayed({ materialSearchBar.clearSuggestions() }, 1000)

                val imm: InputMethodManager? =
                    requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?

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

            override fun OnItemDeleteListener(position: Int, v: View?) {
                materialSearchBar.clearSuggestions()
                materialSearchBar.hideSuggestionsList()
            }
        })
    }


    companion object {
        private const val TAG = "MapFragment"
        private const val DEFAULT_ZOOM = 15f
    }
}
