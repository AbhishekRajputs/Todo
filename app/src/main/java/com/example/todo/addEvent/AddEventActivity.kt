package com.example.todo.addEvent

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.todo.AlarmReceiver
import com.example.todo.dialogFragment.MyDialogFragment
import com.example.todo.R
import com.example.todo.database.AppDatabase
import com.example.todo.databinding.ActivityAddEventBinding
import com.example.todo.eventList.EventListActivity.Companion.EVENT_LIST
import com.example.todo.eventList.EventListActivity.Companion.EXTRA_ALL_EVENT
import com.example.todo.modal.Events
import com.example.todo.scanner.ScannerActivity
import kotlinx.android.synthetic.main.activity_add_event.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList


class AddEventActivity : AppCompatActivity(),
    MyDialogFragment.ItemClickListener {

    private lateinit var eventList: ArrayList<Events>
    lateinit var am:AlarmManager
    lateinit var tp:TimePicker
    lateinit var pi:PendingIntent

    private val calendar by lazy {
        Calendar.getInstance()
    }

    private val database by lazy {
        AppDatabase(this)
    }

    private lateinit var binding: ActivityAddEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_event)

        intent?.let {
            if (it.hasExtra(EXTRA_ALL_EVENT)) {
                eventList = it.getParcelableArrayListExtra<Events>(EXTRA_ALL_EVENT)
            }
        }
        var myIntent:Intent= Intent(this, AlarmReceiver::class.java)
        am = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        bt_scan.setOnClickListener {
            startActivityForResult(Intent(this, ScannerActivity::class.java), SCAN_DATA)
        }

        tv_category.setOnClickListener {
            val tv = MyDialogFragment(this)
            tv.setStyle(
                DialogFragment.STYLE_NORMAL,
                R.style.CustomDialog
            )
            tv.show(supportFragmentManager, "dialog_frag")
        }

        tv_event_date.setOnClickListener {
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]

            val datePickerDialog = DatePickerDialog(
                this,
                OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    tv_event_date.text =
                        dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                }, year, month, day
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        tv_event_time.setOnClickListener {
            val mHour = calendar[Calendar.HOUR_OF_DAY]
            val mMinute = calendar[Calendar.MINUTE]
            val timePickerDialog = TimePickerDialog(
                this,
                OnTimeSetListener { _, hourOfDay, minute ->
                    val isPM = hourOfDay >= 12
                    tv_event_time.text = String.format(
                        "%02d:%02d %s",
                        if (hourOfDay == 12 || hourOfDay == 0) 12 else hourOfDay % 12,
                        minute,
                        if (isPM) "PM" else "AM"
                    )
                },
                mHour,
                mMinute,
                false
            )
            timePickerDialog.show()

        }

        bt_save.setOnClickListener {
            val events = Events()
            events.apply {
                eventCategory = tv_category.text.toString()
                eventDate = tv_event_date.text.toString()
                eventTime = tv_event_time.text.toString()
                eventName = et_event_name.text.toString()
            }

            if (validation()) {
                if (eventList.isNotEmpty()) {
                    for (item in eventList) {
                        if (item.eventTime == events.eventTime) {
                            Toast.makeText(
                                this,
                                "A event is already added in this time", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    val intent = Intent()
                    intent.putExtra("event_data", events)
                    setResult(EVENT_LIST, intent)
                    //save data in the database
                    CoroutineScope(Dispatchers.IO).async {
                        database.todoDao().insertAll(events)
                    }
                    myIntent.putExtra("extra","on")
                    pi= PendingIntent.getBroadcast(this,0,myIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    am.setExact(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,pi)
                    finish()
                }
            }
        }

        bt_cancel.setOnClickListener {
            finish()
        }
    }


    private fun validation(): Boolean {
        return when {
            tv_category.text.toString().isEmpty() -> {
                Toast.makeText(
                    this,
                    "event category should not be empty", Toast.LENGTH_SHORT
                ).show()
                false
            }
            tv_event_date.text.toString().isEmpty() -> {
                Toast.makeText(
                    this,
                    "event date should not be empty", Toast.LENGTH_SHORT
                ).show()
                false
            }
            tv_event_time.text.toString().isEmpty() -> {
                Toast.makeText(
                    this,
                    "event time should not be empty", Toast.LENGTH_SHORT
                ).show()
                false
            }
            et_event_name.text.toString().isEmpty() -> {
                Toast.makeText(
                    this,
                    "event name should not be empty", Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> {
                true
            }
        }
    }

    override fun itemClick(value: String) {
        tv_category.text = value
        val fragment = supportFragmentManager.findFragmentByTag("dialog_frag")
        if (fragment != null) {
            val dialog = fragment as DialogFragment?
            dialog!!.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCAN_DATA && data != null) {
            binding.events = data.getParcelableExtra("scan_data")
        }
    }

    companion object {
        const val SCAN_DATA = 8000
    }
}
