package com.example.websentinel.domain

import java.util.*


data class TaskModel (
    var id: String,
    var title: String,
    var dateCreated: Date,
    var description: String,
    var location: String,
    var dueDate: Date,
    var status: String
) {
    override fun toString(): String {
        return "Task(title='$title', description='$description', date created='$dateCreated', location='$location', status='$status')"
    }
}