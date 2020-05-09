package com.example.doroute.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.doroute.domain.TaskRepository

class TaskManagerViewModelFactory(private val taskRepository: TaskRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TaskManagerViewModel(taskRepository) as T
    }

}