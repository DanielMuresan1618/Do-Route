package com.example.websentinel.data.database

import com.example.websentinel.domain.TaskModel
import com.example.websentinel.domain.TaskRepository

abstract class TaskDbStore(private val appDatabase: AppDatabase) : TaskRepository {

    override fun getAll(): List<TaskModel> {
        return appDatabase.taskDao().getAll().map { it.toDomainModel() }
    }

    override fun addTask(task: TaskModel) {
        appDatabase.taskDao().insertTask(task.toDbModel())
    }

    override fun removeTask(task: TaskModel) {
        appDatabase.taskDao().deleteTask(task.toDbModel())
    }

    private fun TaskModel.toDbModel() = TaskEntity(id, title, dateCreated, description,  location, dueDate ,status )
    private fun TaskEntity.toDomainModel() = TaskModel(id, title, dateCreated ,description,  location, dueDate, status )
}