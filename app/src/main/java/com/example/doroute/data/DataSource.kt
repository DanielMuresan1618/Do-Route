package com.example.doroute.data

import com.example.doroute.domain.TaskModel
import com.example.doroute.domain.TaskRepository
import java.util.*
import java.util.UUID.randomUUID
import kotlin.collections.ArrayList

class DataSource :TaskRepository {
    override fun getAll(): List<TaskModel> {
        val list = ArrayList<TaskModel>()
        list.add(
            TaskModel(
                randomUUID().toString(),
                "Taskul1",
                Date(222020),
                "Descrierea de la taskul 1",
                "Location x",
                Date(322020),
                "Undone"

            )
        )
        list.add(
            TaskModel(
                randomUUID().toString(),
                "Taskul2",
                Date(11111020),
                "Descrierea de la taskul 2",
                "Location y",
                Date(322020),
                "Done"
            )
        )
        return list
    }

    override fun addTask(task: TaskModel) {

    }

    override fun removeTask(task: TaskModel) {

    }

    override fun updateTask(task: TaskModel) {

    }
}