package com.example.doroute.data.models

import java.util.*

data class TaskModel (
    var taskId: String,
    var locationId: String,
    var statusId: String,
    var title: String,
    var description: String,
    var dueDate: Date,
    var latitude: Double,
    var longitude: Double,
    var locatioName: String,
    var address: String,
    var taskState: String
)