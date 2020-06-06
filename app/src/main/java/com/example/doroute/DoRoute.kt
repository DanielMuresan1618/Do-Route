package com.example.doroute

import android.app.Application
import androidx.core.app.NotificationManagerCompat
import com.example.doroute.helpers.NotificationHelper

class DoRoute : Application() {
    companion object {
        lateinit var instance: DoRoute
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        NotificationHelper.createNotificationChannel(
            this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT, true,
            getString(R.string.application_name), "App default notification channel."
        )
        NotificationHelper.createNotificationChannel(
            this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            "OVERDUE", "Overdue tasks notification channel."
        )
        NotificationHelper.createNotificationChannel(
            this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            "PENDING", "Pending tasks notification channel."
        )
        NotificationHelper.createNotificationChannel(
            this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            "COMPLETE", "Completed tasks notification channel."
        )
    }
}