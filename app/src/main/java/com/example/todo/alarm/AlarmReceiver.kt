package com.example.todo.alarm

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Vibrator
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.example.todo.R


class AlarmReceiver : BroadcastReceiver() {
    var notificationManager: NotificationManager? = null
    var myNotification: Notification? = null
    override fun onReceive(context: Context, intent: Intent) {

        val mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI)
        mediaPlayer.start()

        val vibrator = context
            .getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(3000)


        val myIntent = Intent(context, NotificationActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            myIntent,
            0
        )
        myNotification = NotificationCompat.Builder(context)
            .setContentTitle("Wake UP!!!")
            .setContentText("Alarm is belling")
            .setTicker("Notification!")
            .setWhen(System.currentTimeMillis())
            .setDefaults(Notification.FLAG_INSISTENT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.clock)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()

        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager!!.notify(MY_NOTIFICATION_ID, myNotification)
    }

    companion object {
        private const val MY_NOTIFICATION_ID = 1
    }
}
