package com.example.websentinel

import android.location.Location
import java.util.*
import kotlin.collections.ArrayList

class DataSource {
    companion object{

        fun createDataSet(): ArrayList<TaskModel>{
            val list = ArrayList<TaskModel>()
            list.add(
                TaskModel(
                "Taskul1",
                "Descrierea de la taskul 1",
                    Date(222020),
                    Date(322020),
                    "Location x",
                    "Undone"

            )
            )
            list.add(
                TaskModel(
                    "Taskul2",
                    "Descrierea de la taskul 1",
                    Date(22289020),
                    Date(32200020),
                    "Location x",
                    "Undone"

                )
            )
            return list
        }
    }
}