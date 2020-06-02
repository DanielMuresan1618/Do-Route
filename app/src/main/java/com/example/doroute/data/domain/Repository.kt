package com.example.doroute.data.domain

import com.example.doroute.data.models.TaskModel
import com.google.android.gms.maps.model.LatLng


interface Repository {
    //Repository: what I want to do with the database, whichever it will be

    fun getAllTasks():List<TaskModel>
    fun getTaskById(id:String) : TaskModel
    fun getTaskByLocation(latLng: LatLng):TaskModel
    fun addTask(t: TaskModel)
    fun removeTask(t: TaskModel)
    fun updateTask(t: TaskModel)
}