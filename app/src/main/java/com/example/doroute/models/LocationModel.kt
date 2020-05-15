package com.example.doroute.models

import com.google.android.gms.maps.model.LatLng

data class LocationModel(
     var locationId: String,
     var latitude: Double,
     var longitude: Double,
     var name: String,
     var address: String
)