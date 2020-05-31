package com.example.doroute.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.doroute.R
import com.example.doroute.data.models.TaskModel
import com.example.doroute.helpers.TaskStates
import java.util.*
import java.util.Calendar.*

object AlarmScheduler {


    fun scheduleAlarmsForTask(context: Context, task: TaskModel) {
        val day = task.dueDate
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = createPendingIntent(context, task, day)
        scheduleAlarm(task, alarmIntent, alarmMgr)
    }

    private fun scheduleAlarm(
        task: TaskModel,
        alarmIntent: PendingIntent?,
        alarmMgr: AlarmManager
    ) {
        val today = getInstance(Locale.getDefault())

        // Set up the time to schedule the alarm
        val datetimeToAlarm = getInstance(Locale.getDefault())
        datetimeToAlarm.time = task.dueDate

        // Compare the datetimeToAlarm to today
        if (today.before(datetimeToAlarm)) {
            alarmMgr.setRepeating(
                AlarmManager.RTC_WAKEUP,
                datetimeToAlarm.timeInMillis,
                (1000 * 60 * 60 * 2).toLong(), //alarm reschedules automatically from 2 in 2 hours
                alarmIntent
            )
        }
    }

    private fun createPendingIntent(context: Context, task: TaskModel, day: Date): PendingIntent? {
        // create the intent using a unique type
        val status = TaskStates.getStateForValue(task.status)
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            action = context.getString(R.string.action_execute_task)
            type = "$day-${task.title}-${task.locationName}-$status-create"
            putExtra(TaskModel.TASKID, task.taskId)
        }

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    fun updateAlarmsForReminder(context: Context, task: TaskModel) {
        if (!task.checkboxChecked) {
            scheduleAlarmsForTask(context, task)
        } else {
            removeAlarmsForTask(context, task)
        }
    }

    fun removeAlarmsForTask(context: Context, task: TaskModel) {
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java)
        intent.action = context.getString(R.string.action_execute_task)
        intent.putExtra(TaskModel.TASKID, task.taskId)

        // type must be unique so Intent.filterEquals passes the check to make distinct PendingIntents
        // Schedule the alarms based on the days to administer the medicine
        val status = TaskStates.getStateForValue(task.status)
        val type = "${task.dueDate}-${task.title}-${task.locationName}-$status-remove"

        intent.type = type
        val alarmIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.cancel(alarmIntent)
    }
}

