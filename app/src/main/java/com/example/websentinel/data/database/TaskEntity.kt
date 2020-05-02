package com.example.websentinel.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id:UUID,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "date_created")
    val dateCreated: Date,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "location")
    val location: String,

    @ColumnInfo(name = "due_date")
    val dueDate: Date,

    @ColumnInfo(name = "status")
    val status: String
)