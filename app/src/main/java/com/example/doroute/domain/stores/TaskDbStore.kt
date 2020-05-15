package com.example.doroute.domain.stores

import android.util.Log
import com.example.doroute.data.database.AppDatabase
import com.example.doroute.data.database.entities.TaskEntity
import com.example.doroute.models.TaskModel
import com.example.doroute.domain.Repository

class TaskDbStore(private val appDatabase: AppDatabase) : Repository<TaskModel> {


    override fun getAll(): List<TaskModel> {
        return appDatabase.taskDao().getAll().map { it.toDomainModel() }
    }

    override fun get(id: String): TaskModel {
        return appDatabase.taskDao().get(id).toDomainModel()
    }

    override fun add(t: TaskModel) {
        appDatabase.taskDao().insert(t.toDbModel())
    }

    override fun remove(t: TaskModel) {
        appDatabase.taskDao().delete(t.toDbModel())
    }

    override fun update(t: TaskModel) {
        appDatabase.taskDao().update(t.toDbModel())
    }

    private fun TaskModel.toDbModel() =
        TaskEntity(taskId, locationId, statusId, title, description, dueDate)

    private fun TaskEntity.toDomainModel() =
        TaskModel(taskId, locationId, statusId, title, description, dueDate)

    companion object {
        const val TAG = "TaskDbStore"
    }

}