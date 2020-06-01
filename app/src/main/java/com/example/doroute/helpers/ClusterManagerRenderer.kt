package com.example.doroute.helpers

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import com.example.doroute.R
import com.example.doroute.data.models.ClusterMarker
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator

//Render Custom marker using Clusters
class ClusterManagerRenderer(
    context: Context, googleMap: GoogleMap?,
    clusterManager: ClusterManager<ClusterMarker>?
): DefaultClusterRenderer<ClusterMarker>(context, googleMap, clusterManager) {

    private val iconGenerator = IconGenerator(context.applicationContext)
    private val imageView: ImageView
    private val markerWidth: Int
    private val markerHeight: Int

    override fun onBeforeClusterItemRendered(
        item: ClusterMarker,
        markerOptions: MarkerOptions
    ){
        imageView.setImageResource(item.iconPicture)
        val icon = iconGenerator.makeIcon()
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.title)
    }

    override fun shouldRenderAsCluster(cluster: Cluster<ClusterMarker>?): Boolean {
        return false
    }

    fun setUpdateMarker(clusterMarker: ClusterMarker) {
        val marker: Marker? = getMarker(clusterMarker)
        marker?.position = clusterMarker.position
    }

    init {
        // initialize cluster item icon generator
        imageView = ImageView(context.applicationContext)
        markerWidth = context.resources.getDimension(R.dimen.custom_marker_image).toInt()
        markerHeight = context.resources.getDimension(R.dimen.custom_marker_image).toInt()
        imageView.layoutParams = ViewGroup.LayoutParams(markerWidth, markerHeight)
        val padding = context.resources.getDimension(R.dimen.custom_marker_padding).toInt()
        imageView.setPadding(padding, padding, padding, padding)
        iconGenerator.setContentView(imageView)
    }
}