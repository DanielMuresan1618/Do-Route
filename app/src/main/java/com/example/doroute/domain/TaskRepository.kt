package com.example.doroute.domain


interface TaskRepository {
    //Repository: what I want to do with the database, whichever it will be
    fun getAll() : List<TaskModel>
    fun addTask(task: TaskModel)
    fun removeTask(task: TaskModel)
    fun updateTask(task: TaskModel)
}