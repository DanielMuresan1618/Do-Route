package com.example.doroute.view.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.doroute.R
import com.example.doroute.data.database.RoomDatabase
import com.example.doroute.data.domain.stores.TaskDbStore
import com.example.doroute.data.models.TaskModel
import com.example.doroute.viewmodel.TaskViewModel
import com.example.doroute.viewmodel.TaskViewModelFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsFragment : SupportMapFragment(), OnMapReadyCallback, View.OnClickListener {
    //Navigation doesn't show until I inflate it
    private lateinit var mMap : GoogleMap
    private var mapReady = false
    private lateinit var viewModel: TaskViewModel
    private lateinit var tasks: List<TaskModel>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val factory =
            TaskViewModelFactory(
                TaskDbStore(RoomDatabase.getDb(this.requireContext()
                    )
                )
            )
        activity.let {
            ViewModelProvider(this, factory).get(TaskViewModel::class.java) //.of(this) is deprecated!!
        }

        viewModel.tasksLiveData.observe(viewLifecycleOwner, Observer {
                tasks -> this.tasks = tasks
            updateMap()
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView =  inflater.inflate(R.layout.fragment_maps, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
                googleMap -> mMap = googleMap
            mapReady = true
            updateMap()
        }
        return rootView
    }


    private fun updateMap() {

    }

    override fun onMapReady( googleMap: GoogleMap) {
        Toast.makeText(this.requireContext(), "Map is Ready", Toast.LENGTH_SHORT).show()
        //Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap


    }

    override fun onClick(v: View?) {
      //  TODO("Not yet implemented")
    }



}
