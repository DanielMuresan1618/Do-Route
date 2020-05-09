package com.example.doroute.data.database

import android.util.Log
import com.example.doroute.domain.TaskModel
import com.example.doroute.domain.TaskRepository

 class TaskDbStore(private val appDatabase: AppDatabase) : TaskRepository {

    override fun getAll(): List<TaskModel> {
        Log.d("TaskDbStore", "retrieving task")
        return appDatabase.taskDao().getAll().map { it.toDomainModel() }
    }

    override fun addTask(task: TaskModel) {
        Log.d("TaskDbStore", "adding task")
        appDatabase.taskDao().insertTask(task.toDbModel())
    }

    override fun removeTask(task: TaskModel) {
        Log.d("TaskDbStore", "removing task")
        appDatabase.taskDao().deleteTask(task.toDbModel())
    }

     override fun updateTask(task: TaskModel) {
         Log.d("TaskDbStore", "updating task")
         appDatabase.taskDao().updateTask(task.toDbModel())
     }

     private fun TaskModel.toDbModel() = TaskEntity(id, title, dateCreated, description,  location, dueDate ,status )
    private fun TaskEntity.toDomainModel() = TaskModel(id, title, dateCreated ,description,  location, dueDate, status )
}