package com.example.doroute.data.domain

import com.example.doroute.data.models.TaskModel


interface Repository {
    //Repository: what I want to do with the database, whichever it will be

    fun getAllTasks():List<TaskModel>
    fun getTask(id:String) : TaskModel
    fun addTask(t: TaskModel)
    fun removeTask(t: TaskModel)
    fun updateTask(t: TaskModel)
}