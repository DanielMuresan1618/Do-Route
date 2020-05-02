package com.example.websentinel.data

import com.example.websentinel.domain.TaskModel
import java.util.*
import java.util.UUID.randomUUID
import kotlin.collections.ArrayList

class DataSource {
    companion object{

        fun createDataSet(): ArrayList<TaskModel>{
            val list = ArrayList<TaskModel>()
            list.add(
                TaskModel(
                    randomUUID(),
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
                    randomUUID(),
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
    }
}