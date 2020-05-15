package com.example.doroute.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doroute.data.models.TaskModel
import com.example.doroute.data.domain.Repository
import java.util.*

class TaskViewModel(private val repository: Repository<TaskModel>) : ViewModel() {
    //1) Livedata
    val tasksLiveData = MutableLiveData<List<TaskModel>>() //all tasks from the RecyclerView
    val taskLiveData = MutableLiveData<TaskModel>() //the current task

    //2) Communication with the Repository
    fun retrieveTasks() {
        val tasks = repository.getAll()
        tasksLiveData.postValue(tasks)
    }

    fun addTask(taskId: String, locationId:String, statusId: String, title:String, description:String, dueDate:Date) {
        repository.add(
            TaskModel(taskId,locationId,statusId,title,description,dueDate)
        )
        retrieveTasks()
    }

    fun removeTask(task: TaskModel) {
        repository.remove(task)
        retrieveTasks()
    }

    fun updateTask(task: TaskModel){
        repository.update(task)
        retrieveTasks()
    }
}
