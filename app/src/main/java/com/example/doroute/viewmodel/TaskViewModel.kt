package com.example.doroute.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doroute.domain.TaskModel
import com.example.doroute.domain.TaskRepository
import java.util.*

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {
    //1) Livedata
    val tasksLiveData = MutableLiveData<List<TaskModel>>() //all tasks from the RecyclerView
    val taskLiveData = MutableLiveData<TaskModel>() //the current task

    //2) Communication with the Repository
    fun retrieveTasks() {
        val tasks = taskRepository.getAll()
        tasksLiveData.postValue(tasks)
    }

    fun addTask(id: String, title: String, dueDate: Date, description: String, location:String, status:String) {
        taskRepository.addTask(TaskModel(id, title, Calendar.getInstance().time, description,location,dueDate,status))
        retrieveTasks()
    }

    fun removeTask(task: TaskModel) {
        taskRepository.removeTask(task)
        retrieveTasks()
    }

    fun updateTask(task:TaskModel){
        taskRepository.updateTask(task)
        retrieveTasks()
    }
}
