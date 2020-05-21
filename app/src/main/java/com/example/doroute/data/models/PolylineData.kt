package com.example.doroute.data.models

import com.google.android.gms.maps.model.Polyline
import com.google.maps.model.DirectionsLeg

data class PolylineData(
    var polyline: Polyline,
    var leg: DirectionsLeg
)