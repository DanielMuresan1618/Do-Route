package com.example.doroute.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_states")
data class StateEntity (
    @PrimaryKey
    val stateId: String,

    @ColumnInfo(name="taskId")
    var taskId: String,

    @ColumnInfo(name="name")
    val name:String
)