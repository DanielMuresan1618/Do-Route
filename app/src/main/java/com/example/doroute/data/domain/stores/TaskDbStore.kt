package com.example.doroute.data.domain.stores

import com.example.doroute.data.database.AppDatabase
import com.example.doroute.data.database.entities.FullTaskEntity
import com.example.doroute.data.database.entities.LocationEntity
import com.example.doroute.data.database.entities.TaskEntity
import com.example.doroute.data.models.TaskModel
import com.example.doroute.data.domain.Repository

class TaskDbStore(private val appDatabase: AppDatabase) : Repository {


    override fun getAllTasks(): List<TaskModel> {
        return appDatabase.taskDao().getAllTasks().map { it.toDomainModel() }
    }

    override fun getTask(id: String): TaskModel {
        return appDatabase.taskDao().getTask(id).toDomainModel()
    }

    override fun addTask(t: TaskModel) {
        appDatabase.taskDao().insertTask(t.getTask(), t.getLocation())
    }

    override fun removeTask(t: TaskModel) {
        appDatabase.taskDao().deleteTask(t.getTask(), t.getLocation())
    }

    override fun updateTask(t: TaskModel) {
        appDatabase.taskDao().updateTask(t.getTask(), t.getLocation())
    }

    private fun TaskModel.getTask() =
        TaskEntity(taskId, title, description, dueDate, status, checkboxChecked)

    private fun TaskModel.getLocation() =
        LocationEntity(locationId, taskId, latitude, longitude, locationName)



    private fun FullTaskEntity.toDomainModel() =
        TaskModel(
            task.taskId,
            location.locationId,
            task.title,
            task.description,
            task.dueDate,
            location.latitude,
            location.longitude,
            location.name,
            task.status,
            task.checked
        )

    companion object {
        const val TAG = "TaskDbStore"
    }
}