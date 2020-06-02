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
    var checkboxChecked: Boolean = false,
    var tripActive: Boolean = false
){
    companion object {
        const val TASKID = "taskId"
        const val CHECKBOXCHECKED = "checkboxChecked"
        const val TRIPACTIVE = "tripActive"
//        const val LOCATIONID = "locationId"
//        const val TITLE = "title"
//        const val DESCRIPTION = "description"
//        const val DUEDATE = "dueDate"
//        const val LATITUDE = "latitude"
//        const val LONGITUDE = "longitude"
//        const val LOCATIONNAME = "locationName"
//        const val STATUS = "status"

    }
}
