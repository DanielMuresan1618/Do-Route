package com.example.doroute.ui.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doroute.DoRoute
import com.example.doroute.data.models.TaskModel
import com.example.doroute.data.domain.Repository
import com.example.doroute.helpers.TaskStates
import com.example.doroute.reminders.AlarmScheduler
import java.util.*

class TaskViewModel(private val repository: Repository) : ViewModel() {
    //1) Livedata
    val tasksLiveData = MutableLiveData<List<TaskModel>>()

    //2) Communication with the Repository
    fun retrieveTasks() {
        val tasks = repository.getAllTasks()
        tasksLiveData.postValue(tasks)
    }

    fun addTask(task:TaskModel,  context: Context) {
        repository.addTask(task)
        retrieveTasks()
        AlarmScheduler.scheduleAlarmsForTask(context, task)
    }

    fun removeTask(task: TaskModel, context: Context) {
        repository.removeTask(task)
        retrieveTasks()
        AlarmScheduler.removeAlarmsForTask(context,task)
    }

    fun updateTask(task: TaskModel,  context: Context){
        repository.updateTask(task)
        retrieveTasks()
        AlarmScheduler.updateAlarmsForReminder(context, task)
    }
}
