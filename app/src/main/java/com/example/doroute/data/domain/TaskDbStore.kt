package com.example.doroute.data.domain

import com.example.doroute.data.database.AppDatabase
import com.example.doroute.data.database.TaskEntity
import com.example.doroute.data.models.TaskModel
import com.google.android.gms.maps.model.LatLng

class TaskDbStore(private val appDatabase: AppDatabase) : Repository {


    override fun getAllTasks(): List<TaskModel> {
        return appDatabase.taskDao().getAllTasks().map { it.toDomainModel() }
    }

    override fun getTaskById(id: String): TaskModel {
        return appDatabase.taskDao().getTask(id).toDomainModel()
    }

    override fun getTaskByLocation(latLng: LatLng): TaskModel {
        return appDatabase.taskDao().getTaskByLocation(latLng.latitude, latLng.longitude).toDomainModel()
    }

    override fun addTask(t: TaskModel) {
        appDatabase.taskDao().insertTask(t.toDbModel())
    }

    override fun removeTask(t: TaskModel) {
        appDatabase.taskDao().deleteTask(t.toDbModel())
    }

    override fun updateTask(t: TaskModel) {
        appDatabase.taskDao().updateTask(t.toDbModel())
    }

    private fun TaskModel.toDbModel() =
        TaskEntity(
            taskId,
            title,
            description,
            location.latitude,
            location.longitude,
            dueDate,
            status,
            checkboxChecked,
            tripActive
        )

    private fun TaskEntity.toDomainModel() =
        TaskModel(
            taskId,
            title,
            description,
            dueDate,
            LatLng(latitude, longitude),
            status,
            checked,
            trip
        )
}