package com.example.doroute.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val taskId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "due_date")
    val dueDate: Date,

    @ColumnInfo(name = "status")
    val status: Int,

    @ColumnInfo(name = "checked")
    val checked: Boolean,

    @ColumnInfo(name = "trip")
    val trip: Boolean
)


