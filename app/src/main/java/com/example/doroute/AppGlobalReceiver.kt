package com.example.doroute

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.doroute.data.domain.stores.TaskDbStore
import com.example.doroute.data.models.TaskModel
import com.example.doroute.helpers.TaskStates
import com.example.doroute.reminders.AlarmScheduler

//Handles any global application broadcasts.
class AppGlobalReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "AppGlobalReceiver"
        const val NOTIFICATION_ID = "notification_id"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive reached")
        if (context != null && intent != null && intent.action != null) {
            Log.d(TAG, "first if reached")
            // Handle the action to execute task
            if (intent.action!!.equals(
                    context.getString(R.string.action_execute_task),
                    ignoreCase = true
                )
            ) {
                Log.d(TAG, "second if reached")
                val extras = intent.extras
                if (extras != null) {
                    val notificationId = extras.getInt(NOTIFICATION_ID)
                    val taskId = extras.getString(TaskModel.TASKID)

                    // Lookup the task in the db
                    val db = TaskDbStore(
                        com.example.doroute.data.database.RoomDatabase.getDb(
                            context.applicationContext
                        )
                    )
                    val task: TaskModel? = db.getTaskById(taskId!!)
                    if (task != null) {
                        Log.d(TAG, "Task Checkbox: ${task.checkboxChecked}")
                        task.checkboxChecked = true
                        task.status = TaskStates.COMPLETE
                        // Update the database
                        db.updateTask(task)
                        Log.d(TAG, "Task Checkbox: ${task.checkboxChecked}")
                        // Remove the alarm
                        AlarmScheduler.removeAlarmsForTask(context, task)
                    }

                    // finally, cancel the notification
                    if (notificationId != -1) {
                        val notificationManager = NotificationManagerCompat.from(context)
                        notificationManager.cancel(notificationId)
                        //  notificationManager.cancelAll() // testing
                    }
                }
            }
        }
    }
}