package com.example.todo.alarmUtils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import java.util.*

object TriggerAlarm {

    fun triggerAlarm ( targetDateTime : Calendar,context: Context) {

        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReciever::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        // cal.add(Calendar.SECOND, 5);
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, targetDateTime.timeInMillis, pendingIntent)
    }
}