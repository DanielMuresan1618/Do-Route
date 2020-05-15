package com.example.doroute.models

import java.util.*

data class TaskModel (
    var taskId: String,
    var locationId: String,
    var statusId: String,
    var title: String,
    var description: String,
    var dueDate: Date
)