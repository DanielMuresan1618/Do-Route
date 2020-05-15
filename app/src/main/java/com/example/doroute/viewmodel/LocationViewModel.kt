package com.example.doroute.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doroute.data.domain.Repository
import com.example.doroute.data.models.LocationModel
import java.net.Inet4Address

class LocationViewModel(private val repository: Repository<LocationModel>) : ViewModel() {
    val locationsLiveData = MutableLiveData<List<LocationModel>>()

    fun retrieveLocation() {
        val locations = repository.getAll()
        locationsLiveData.postValue(locations)
    }

    fun addState(locationId: String,latitude:Double, longitude:Double, name: String, address: String) {
        repository.add(LocationModel(locationId,latitude,longitude, name, address))
    }

    fun removeLocation(location: LocationModel) {
        repository.remove(location)
    }

    fun updateLocation(location: LocationModel) {
        repository.update(location)
    }
}