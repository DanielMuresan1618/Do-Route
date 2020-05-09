package com.example.doroute.view.map

import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback


class MapsFragment : MapFragment(), OnMapReadyCallback, View.OnClickListener {
    private lateinit var mMapView: GoogleMap

    override fun onMapReady(p0: GoogleMap?) {

    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }



}
