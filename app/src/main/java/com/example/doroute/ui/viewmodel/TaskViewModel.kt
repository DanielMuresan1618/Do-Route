package com.example.doroute.ui.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doroute.DoRoute
import com.example.doroute.data.models.TaskModel
import com.example.doroute.data.domain.Repository
import com.example.doroute.helpers.TaskStates
import com.example.doroute.reminders.AlarmScheduler
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import java.util.*

class TaskViewModel(private val repository: Repository) : ViewModel() {
    //1) Livedata
    val tasksLiveData = MutableLiveData<List<TaskModel>>()

    init{
        retrieveTasks()
    }
    //2) Communication with the Repository
    fun retrieveTasks() {
        val tasks = repository.getAllTasks()
        tasksLiveData.postValue(tasks)
    }

    fun getTaskByLocation(latLng: LatLng):TaskModel{
       return repository.getTaskByLocation(latLng)
    }

    fun addTask(task:TaskModel) {
        //I pass context because I need it for the alarm scheduler
        repository.addTask(task)
        retrieveTasks()
        AlarmScheduler.scheduleAlarmsForTask(DoRoute.instance.applicationContext, task)
    }

    fun removeTask(task: TaskModel) {
        AlarmScheduler.removeAlarmsForTask(DoRoute.instance.applicationContext,task)
        repository.removeTask(task)
        retrieveTasks()
    }

    fun updateTask(task: TaskModel){
        repository.updateTask(task)
        retrieveTasks()
        AlarmScheduler.updateAlarmsForReminder(DoRoute.instance.applicationContext, task)
    }
}
