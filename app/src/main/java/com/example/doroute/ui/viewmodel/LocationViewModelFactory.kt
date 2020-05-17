package com.example.doroute.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.doroute.data.domain.Repository
import com.example.doroute.data.models.LocationModel

class LocationViewModelFactory(
    private val repository: Repository<LocationModel>
) : ViewModelProvider.NewInstanceFactory() {
    //I need factories because I inject the repository argument to the viewmodel

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LocationViewModel(repository) as T
    }

}