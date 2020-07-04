package com.example.todo

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.todo.alarmUtils.AlarmReciever
import com.example.todo.alarmUtils.TriggerAlarm
import timber.log.Timber
import java.util.*

class BaseApp : Application() {

    companion object {
        val NOTIFICATION_ID = 1
        val CHANNEL_ID = "com.example.testalaram"
    }

    override fun onCreate() {
        super.onCreate()




        Timber.plant(Timber.DebugTree())

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Notification"
            val descriptionText = "Alarm Triggered"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(BaseApp.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}