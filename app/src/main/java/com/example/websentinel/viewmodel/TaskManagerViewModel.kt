package com.example.websentinel.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.websentinel.domain.TaskModel
import com.example.websentinel.domain.TaskRepository
import java.util.*
import java.util.UUID.randomUUID

class TaskManagerViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    val tasksLiveData = MutableLiveData<List<TaskModel>>()

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
