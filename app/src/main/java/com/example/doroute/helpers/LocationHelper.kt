package com.example.doroute.helpers

import android.content.IntentSender
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.MapView
import com.google.android.gms.tasks.Task

object LocationHelper {
    private const val LOCATION_REQUEST_FASTEST_INTERVAL : Long = 5000
    private const val LOCATION_REQUEST_INTERVAL : Long = 10000

     fun getLocationRequest(): LocationRequest {
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.interval = LOCATION_REQUEST_INTERVAL
        locationRequest.fastestInterval = LOCATION_REQUEST_FASTEST_INTERVAL
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return locationRequest
    }

     fun locationSettingRequest(activity: FragmentActivity, onSuccessCallback: ()-> Unit) {
        //check if gps is enabled or not and then request user to enable it
        val locationRequest: LocationRequest = LocationHelper.getLocationRequest()
        val builder: LocationSettingsRequest.Builder =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient: SettingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener(activity) {
            onSuccessCallback()
            Log.d("LocationHelper object", "location access granted")
        }
            .addOnFailureListener(activity) { e ->
                Log.d("LocationHelper object", "location access denied")
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

     fun findMyLocationButton(mapView: MapView) {
        val locationButton =
            (mapView.findViewById<View>("1".toInt())
                .parent as View).findViewById<View>("2".toInt())

        val layoutParams: RelativeLayout.LayoutParams =
            locationButton.layoutParams as RelativeLayout.LayoutParams
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        layoutParams.setMargins(0, 0, 40, 180)
    }
}