package com.example.doroute.data.models

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class ClusterMarker(
    private val position: LatLng,
    private val title : String,
    private val snippet : String,
     val iconPicture: Int,
     val task: TaskModel
): ClusterItem {
    override fun getSnippet(): String {
        return snippet
    }

    override fun getTitle(): String {
        return title
    }

    override fun getPosition(): LatLng {
        return position
    }

}