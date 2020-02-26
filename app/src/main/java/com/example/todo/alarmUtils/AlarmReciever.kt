package com.example.todo.alarmUtils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Vibrator
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todo.BaseApp
import com.example.todo.R


class AlarmReciever : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        Toast.makeText(context, "Alarm Recieved!", Toast.LENGTH_SHORT).show()

        val vibrator = context
            .getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(2000)

        createAndShowNotification(context)


    }

    fun createAndShowNotification(context: Context) {

        val icon = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)

        val builder = NotificationCompat.Builder(context, BaseApp.CHANNEL_ID)
            .setLargeIcon(icon)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("My Alarm notification")
            .setContentText("Alarm Received")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(BaseApp.NOTIFICATION_ID, builder.build())
        }
    }

}

