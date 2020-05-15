package com.example.doroute.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doroute.data.domain.Repository
import com.example.doroute.data.models.StateModel
import com.example.doroute.data.models.TaskModel
import java.util.*


class StateViewModel(private val repository: Repository<StateModel>) : ViewModel() {
    val statesLiveData = MutableLiveData<List<StateModel>>()

    fun retrieveStates() {
        val states = repository.getAll()
        statesLiveData.postValue(states)
    }

    fun addState(stateId: String, name: String) {
        repository.add(StateModel(stateId,name))
    }


    fun removeState(state: StateModel) {
        repository.remove(state)
    }

    fun updateState(state: StateModel){
        repository.update(state)
    }
}