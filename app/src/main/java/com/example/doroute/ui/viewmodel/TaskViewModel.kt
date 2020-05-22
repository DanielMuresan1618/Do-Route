package com.example.doroute.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doroute.data.models.TaskModel
import com.example.doroute.data.domain.Repository
import java.util.*

class TaskViewModel(private val repository: Repository) : ViewModel() {
    //1) Livedata
    val tasksLiveData = MutableLiveData<List<TaskModel>>() //all tasks from the RecyclerView
    val taskLiveData = MutableLiveData<TaskModel>() //the current task

    //2) Communication with the Repository
    fun retrieveTasks() {
        val tasks = repository.getAllTasks()
        tasksLiveData.postValue(tasks)
    }

    fun addTask(task:TaskModel) {
        repository.addTask(task)
        retrieveTasks()
    }

    fun removeTask(task: TaskModel) {
        repository.removeTask(task)
        retrieveTasks()
    }

    fun updateTask(task: TaskModel){
        repository.updateTask(task)
        retrieveTasks()
    }
}
