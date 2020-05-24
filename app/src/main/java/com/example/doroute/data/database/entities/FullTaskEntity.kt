package com.example.doroute.data.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation


data class FullTaskEntity(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "taskId"
    )
    val location: LocationEntity
    )