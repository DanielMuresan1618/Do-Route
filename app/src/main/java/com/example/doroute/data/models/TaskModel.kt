package com.example.doroute.data.models

import com.example.doroute.helpers.TaskStates
import java.util.*

data class TaskModel (
    var taskId: String,
    var locationId: String,
    var title: String,
    var description: String,
    var dueDate: Date,
    var latitude: Double,
    var longitude: Double,
    var locationName: String,
    var status: Int,
    var checkboxChecked: Boolean = false
)