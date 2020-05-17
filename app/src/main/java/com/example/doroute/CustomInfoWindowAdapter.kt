package com.example.doroute

import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker


class CustomInfoWindowAdapter(
    private val mWindow: View
) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(marker: Marker): View {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoWindow(marker: Marker): View {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

    private fun rendowWindowText(marker: Marker, view: View) {
        val title = marker.title
        val tvTitle = view.findViewById(R.id.custom_info_title) as TextView

        if (!title.isNullOrEmpty()) {
            tvTitle.text = title
        }

        val snippet = marker.snippet
        val tvSnippet = view.findViewById(R.id.custom_info_snippet) as TextView

        if (!snippet.isNullOrEmpty()) {
            tvSnippet.text = snippet
        }
    }
}