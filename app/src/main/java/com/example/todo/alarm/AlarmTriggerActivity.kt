package com.example.todo.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.todo.R
import com.example.todo.alarmUtils.TriggerAlarm
import kotlinx.android.synthetic.main.activity_alaram_trigger.*
import java.util.*

class AlarmTriggerActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_alaram_trigger)
        val now = Calendar.getInstance()
        pickerdate!!.init(
            now[Calendar.YEAR],
            now[Calendar.MONTH],
            now[Calendar.DAY_OF_MONTH],
            null
        )
        pickertime!!.currentHour = now[Calendar.HOUR_OF_DAY]
        pickertime!!.currentMinute = now[Calendar.MINUTE]
        setalarm!!.setOnClickListener {
            val current = Calendar.getInstance()
            val cal = Calendar.getInstance()
            cal[pickerdate!!.year, pickerdate!!.month, pickerdate!!.dayOfMonth, pickertime!!.currentHour, pickertime!!.currentMinute] = 0
            if (cal <= current) {
                //The set Date/Time already passed
                Toast.makeText(
                    applicationContext,
                    "Invalid Date/Time",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                setAlarm(cal)
            }
        }
    }

    private fun setAlarm(targetCal: Calendar) {
        info!!.text = ("""
                ***
                Alarm is set@ """ + targetCal.time + """
                ***
                
                """).trimIndent()

        Toast.makeText(this,"Alarm save",Toast.LENGTH_LONG).show()
        val trigger= TriggerAlarm
        trigger.triggerAlarm(targetCal,this)
        }



}