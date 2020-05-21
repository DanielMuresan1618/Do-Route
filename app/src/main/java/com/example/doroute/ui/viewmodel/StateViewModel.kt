package com.example.doroute.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doroute.data.domain.Repository
import com.example.doroute.data.models.TaskState


class StateViewModel(private val repository: Repository<TaskState>) : ViewModel() {
    val statesLiveData = MutableLiveData<List<TaskState>>()

    fun retrieveStates() {
        val states = repository.getAll()
        statesLiveData.postValue(states)
    }

    fun addState(stateId: String, name: String) {
        repository.add(TaskState(stateId,name))
    }


    fun removeState(taskState: TaskState) {
        repository.remove(taskState)
    }

    fun updateState(taskState: TaskState){
        repository.update(taskState)
    }
}