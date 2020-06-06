package com.example.doroute

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.doroute.data.database.RoomDatabase
import com.example.doroute.data.domain.TaskDbStore
import com.example.doroute.data.models.TaskModel
import com.example.doroute.helpers.TaskStates
import com.example.doroute.reminders.AlarmScheduler

//Handles any global application broadcasts.
class AppGlobalReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null && intent.action != null) {
            // Handle the action to execute task
            if (intent.action!!.equals(
                    context.getString(R.string.action_execute_task),
                    ignoreCase = true
                )
            ) {
                val extras = intent.extras
                if (extras != null) {
                    val notificationId = extras.getInt(NOTIFICATION_ID)
                    val taskId = extras.getString(TaskModel.TASKID)

                    // Lookup the task in the db
                    val db = TaskDbStore(
                        RoomDatabase.getDb(
                            context
                        )
                    )
                    val task: TaskModel? = db.getTaskById(taskId!!)
                    if (task != null) {
                        task.checkboxChecked = true
                        task.status = TaskStates.COMPLETE
                        task.tripActive = false
                        // Update the database
                        db.updateTask(task)
                        // Remove the alarm
                        AlarmScheduler.removeAlarmsForTask(context, task)
                    }

                    // finally, cancel the notification
                    if (notificationId != -1) {
                        val notificationManager = NotificationManagerCompat.from(context)
                        notificationManager.cancel(notificationId)
                    }
                }
            }
        }
    }
    companion object {
        const val TAG = "AppGlobalReceiver"
        const val NOTIFICATION_ID = "notification_id"
    }
}