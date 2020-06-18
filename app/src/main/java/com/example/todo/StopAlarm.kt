package com.example.todo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.todo.RingtoneService.Companion.r
import com.example.todo.eventList.EventListActivity
import kotlinx.android.synthetic.main.activity_stop_alarm.*

class StopAlarm : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_alarm)

        btn_stop.setOnClickListener {
            r.stop()
            intent = Intent(applicationContext, EventListActivity::class.java)
            startActivity(intent)
        }
    }
}
