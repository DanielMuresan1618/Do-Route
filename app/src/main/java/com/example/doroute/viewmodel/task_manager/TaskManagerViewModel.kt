package com.example.doroute.viewmodel.task_manager

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doroute.domain.TaskModel
import com.example.doroute.domain.TaskRepository
import java.util.*

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
