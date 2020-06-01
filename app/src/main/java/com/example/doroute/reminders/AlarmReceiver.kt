package com.example.doroute.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import com.example.doroute.R
import com.example.doroute.data.database.RoomDatabase
import com.example.doroute.data.domain.stores.TaskDbStore
import com.example.doroute.data.models.TaskModel
import com.example.doroute.notifications.NotificationHelper
import java.lang.NullPointerException

class AlarmReceiver : BroadcastReceiver() {
    //emits notification for task
    private val TAG = "AlarmReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive() called with: context = [$context], intent = [$intent]")
        if (context != null && intent != null && intent.action != null) {
            if (intent.action!!.equals(
                    context.getString(R.string.action_execute_task),
                    ignoreCase = true
                )
            ) {
                if (intent.extras != null) {
                    val repository = TaskDbStore(RoomDatabase.getDb(context.applicationContext))
                    try {
                        val task: TaskModel? =
                            repository.getTask(intent.extras!!.getString(TaskModel.TASKID)!!) //highly dangerous operation; there won't be stale data if broadcasts are called from livedata contexts
                        if (task != null) {
                            Log.d(TAG, "Am primit un task ${task.status}")
                            val id = SystemClock.uptimeMillis().toInt()
                            NotificationHelper.createNotificationForTask(context, task, id)
                        }
                    } catch(e: NullPointerException){
                        Log.e(TAG, "Ghost entries are present in the db but not in the UI! ${e.stackTrace}")
                    }
                }
            }
        }
    }
}