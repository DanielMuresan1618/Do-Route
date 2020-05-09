package com.example.doroute.domain


interface TaskRepository {
    fun getAll() : List<TaskModel>
    fun addTask(task: TaskModel)
    fun removeTask(task: TaskModel)
    fun updateTask(task: TaskModel)
}