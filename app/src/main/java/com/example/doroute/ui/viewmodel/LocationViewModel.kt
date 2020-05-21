package com.example.doroute.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doroute.data.domain.Repository
import com.example.doroute.data.models.TaskLocation

class LocationViewModel(private val repository: Repository<TaskLocation>) : ViewModel() {
    val locationsLiveData = MutableLiveData<List<TaskLocation>>()

    fun retrieveLocation() {
        val locations = repository.getAll()
        locationsLiveData.postValue(locations)
    }

    fun addState(locationId: String,latitude:Double, longitude:Double, name: String, address: String) {
        repository.add(TaskLocation(locationId,latitude,longitude, name, address))
    }

    fun removeLocation(taskLocation: TaskLocation) {
        repository.remove(taskLocation)
    }

    fun updateLocation(taskLocation: TaskLocation) {
        repository.update(taskLocation)
    }
}