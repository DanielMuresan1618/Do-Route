package com.example.doroute.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.doroute.AppGlobalReceiver
import com.example.doroute.MainActivity
import com.example.doroute.R
import com.example.doroute.data.models.TaskModel


object NotificationHelper {

    private const val TASK_REQUEST_CODE = 2020

    fun createNotificationChannel(
        context: Context,
        importance: Int,
        showBadge: Boolean,
        status: String,
        description: String
    ) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelId = "${context.packageName}-$status"
            val channel = NotificationChannel(channelId, status, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            // Register the channel with the system
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun createNotificationForTask(context: Context, task: TaskModel, id: Int) {

        // create the task notification
        val notificationBuilder =
            buildNotificationForTask(
                context,
                task
            )

        // add an action to the task notification
        val taskPendingIntent =
            createPendingIntentForAction(
                context,
                task,
                id
            )
        notificationBuilder.addAction(R.drawable.ic_done_green_24dp, "Do it!", taskPendingIntent)

        // call notify
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(id, notificationBuilder.build())
    }


    private fun buildNotificationForTask(
        context: Context,
        task: TaskModel
    ): NotificationCompat.Builder {
        val status = TaskStates.getStateForValue(task.status)
        val channelId = "${context.packageName}-${status}"

        return NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.ic_doroute_logo)
            setContentTitle(task.title)
            setAutoCancel(true)

            // get a drawable reference for the LargeIcon
            val drawable = when (task.status) {
                TaskStates.COMPLETE -> R.drawable.ic_done_green_24dp
                TaskStates.OVERDUE -> R.drawable.ic_overdue_red_24dp
                TaskStates.PENDING -> R.drawable.ic_pending_yellow_24dp
                else -> R.drawable.amu_bubble_mask
            }
            setLargeIcon(BitmapFactory.decodeResource(context.resources, drawable))
            setContentText("${task.title}, ${task.description}")

            // Launches the app to open the reminder edit screen when tapping the whole notification
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(TaskModel.TASKID, task.taskId)
            }

            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            setContentIntent(pendingIntent)
        }
    }

    private fun createPendingIntentForAction(
        context: Context,
        task: TaskModel,
        id: Int
    ): PendingIntent? {
        /*
            Create an Intent to update the task if action_execute_task is clicked
         */
        val executeTaskIntent = Intent(context, AppGlobalReceiver::class.java).apply {
            action = context.getString(R.string.action_execute_task)
            putExtra(AppGlobalReceiver.NOTIFICATION_ID, id)
            putExtra(TaskModel.TASKID, task.taskId)
            putExtra(TaskModel.CHECKBOXCHECKED, true)
        }

        return PendingIntent.getBroadcast(
            context,
            TASK_REQUEST_CODE, executeTaskIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}