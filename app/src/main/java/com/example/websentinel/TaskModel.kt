package com.example.websentinel

import android.location.Location
import java.util.*

data class TaskModel (
    var title: String,
    var description: String,
    var dateCreated: Date,
    var dueDate: Date,
    var location: String,
    var status: String
) {
    override fun toString(): String {
        return "Task(title='$title', description='$description', date created='$dateCreated', location='$location', status='$status')"
    }
}