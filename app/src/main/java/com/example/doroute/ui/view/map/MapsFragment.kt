package com.example.doroute.ui.view.map

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity.RESULT_OK
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
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
import com.example.doroute.R
import com.example.doroute.data.models.LocationModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.*
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import java.util.*


class MapsFragment : Fragment(), OnMapReadyCallback {
    private var mapReady: Boolean=false
    private var mLastKnownLocation: Location? = null
    private lateinit var materialSearchBar: MaterialSearchBar
    private var mMap: GoogleMap? =null
    private var mLocationPermissionsGranted = false
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLocation: LocationModel? = null
    private var mMarker: Marker? = null
    private var mapView: MapView? = null
    private lateinit var rootView: View
    private lateinit var placeFields: List<Place.Field>
    private var predictionList: List<AutocompletePrediction>? = null
    private lateinit var placesClient: PlacesClient


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_maps, container, false)
        return rootView
    }

    private fun updateMap() {
        if (mapReady ) {
            //do stuff
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView= rootView.findViewById(R.id.map)
        mapView!!.onCreate(savedInstanceState)
        mapView!!.onResume()
        mapView!!.getMapAsync (this)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
        Places.initialize(this.requireActivity(), getString(R.string.mapsKey))
        placesClient = Places.createClient(this.requireContext())
        initSearchBar()


    }



    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        mMap!!.isMyLocationEnabled = true
        mMap!!.uiSettings.isMyLocationButtonEnabled = true

        val activity = requireActivity()

        if (mapView?.findViewById<View?>("1".toInt()) != null) {
            val locationButton =
                (mapView!!.findViewById<View>("1".toInt())
                    .parent as View).findViewById<View>("2".toInt())
            val layoutParams: RelativeLayout.LayoutParams =
                locationButton.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 40, 180)


        }

        //check if gps is enabled or not and then request user to enable it

        //check if gps is enabled or not and then request user to enable it
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient: SettingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener(requireActivity()) { getDeviceLocation() }

        task.addOnFailureListener(activity,
            OnFailureListener { e ->
                if (e is ResolvableApiException) {
                    val resolvable: ResolvableApiException = e
                    try {
                        resolvable.startResolutionForResult(activity, 51)
                    } catch (e1: IntentSender.SendIntentException) {
                        e1.printStackTrace()
                    }
                }
            })

        mMap!!.setOnMyLocationButtonClickListener {
            if (materialSearchBar.isSuggestionsVisible) materialSearchBar.clearSuggestions()
            if (materialSearchBar.isSearchEnabled) materialSearchBar.disableSearch()
            false
        }


    }

    private fun initSearchBar() {
        materialSearchBar = rootView.findViewById(R.id.searchBar)
        materialSearchBar.setOnSearchActionListener(object : OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {}
            override fun onSearchConfirmed(text: CharSequence) {
                //TODO: this line is very dangerous!
                requireActivity().startSearch(text.toString(), true, null, true)
            }

            override fun onButtonClicked(buttonCode: Int) {
                when(buttonCode) {
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

                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val predictionsResponse: FindAutocompletePredictionsResponse? = task.result
                            if (predictionsResponse != null) {
                                predictionList =
                                    predictionsResponse.autocompletePredictions
                                val suggestionsList: List<String>?
                                suggestionsList = predictionList?.map { prediction -> prediction.getFullText(null).toString() }

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

        materialSearchBar.setSuggstionsClickListener(object : SuggestionsAdapter.OnItemViewClickListener {
            override fun OnItemClickListener(position: Int, v: View?) {
                if (position >= predictionList!!.size) {
                    return
                }
                val selectedPrediction = predictionList!![position]
                val suggestion =
                    materialSearchBar.lastSuggestions[position].toString()
                materialSearchBar.text = suggestion
                Handler().postDelayed(Runnable { materialSearchBar.clearSuggestions() }, 1000)

                //TODO: another troublesome line of code!
                val imm: InputMethodManager? = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?

                 imm?.hideSoftInputFromWindow(
                    materialSearchBar.windowToken,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )

                val placeId = selectedPrediction.placeId
                val placeFields: List<Place.Field> = listOf(Place.Field.LAT_LNG, Place.Field.NAME )
                val fetchPlaceRequest =
                    FetchPlaceRequest.builder(placeId, placeFields).build()
                placesClient.fetchPlace(fetchPlaceRequest)
                    .addOnSuccessListener(object : OnSuccessListener<FetchPlaceResponse?> {
                      override  fun onSuccess(fetchPlaceResponse: FetchPlaceResponse?) {
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
                        }
                    }).addOnFailureListener(object : OnFailureListener {
                        override fun onFailure( e: Exception) {
                            if (e is ApiException) {
                                val apiException =
                                    e
                                apiException.printStackTrace()
                                val statusCode = apiException.statusCode
                                Log.i("mytag", "place not found: " + e.message)
                                Log.i("mytag", "status code: $statusCode")
                            }
                        }
                    })
            }

           override fun OnItemDeleteListener(position: Int, v: View?) {}
        })
    }

    private fun getDeviceLocation() {
        mFusedLocationProviderClient!!.lastLocation
            .addOnCompleteListener(object : OnCompleteListener<Location?> {
                override fun onComplete(task: Task<Location?>) {
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
                            val locationRequest = LocationRequest.create()
                            locationRequest.interval = 10000
                            locationRequest.fastestInterval = 5000
                            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            val locationCallback = object : LocationCallback() {
                                override fun onLocationResult(locationResult: LocationResult?) {
                                    super.onLocationResult(locationResult)
                                    if (locationResult == null) {
                                        return
                                    }
                                    mLastKnownLocation = locationResult.lastLocation
                                    if (mLastKnownLocation!=null){
                                        mMap!!.moveCamera(
                                            CameraUpdateFactory.newLatLngZoom(
                                                LatLng(
                                                    mLastKnownLocation!!.latitude,
                                                    mLastKnownLocation!!.longitude
                                                ), DEFAULT_ZOOM
                                            )
                                        )
                                        mFusedLocationProviderClient?.removeLocationUpdates(this)
                                    }

                                }
                            }
                            mFusedLocationProviderClient!!.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                null
                            )
                        }
                    } else {
                        Toast.makeText(
                            activity,
                            "unable to get last location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation()
            }
        }
    }

    companion object {
        private const val TAG = "MapFragment"
        private const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1234
        private const val DEFAULT_ZOOM = 15f
        private const val PLACE_PICKER_REQUEST = 1
        private val LAT_LNG_BOUNDS = LatLngBounds(
            LatLng(-40.0, -168.0),
            LatLng(71.0, 136.0)
        )
    }

}
