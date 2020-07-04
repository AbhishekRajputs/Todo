package com.example.todo.addEvent

import android.app.AlarmManager
import android.app.Activity

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
import com.example.todo.alarm.AlarmTriggerActivity
import com.example.todo.barCode.BarcodeReaderActivity
import com.example.todo.barCode.BarcodeReaderFragment
import com.example.todo.database.AppDatabase
import com.example.todo.databinding.ActivityAddEventBinding
import com.example.todo.eventList.EventListActivity.Companion.EVENT_LIST
import com.example.todo.eventList.EventListActivity.Companion.EXTRA_ALL_EVENT
import com.example.todo.modal.Events
import com.google.android.gms.vision.barcode.Barcode
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


    private val BARCODE_READER_ACTIVITY_REQUEST: Int =1008

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
            val launchIntent = BarcodeReaderActivity.getLaunchIntent(this, true, false)
            startActivityForResult(launchIntent, BARCODE_READER_ACTIVITY_REQUEST)
        }

        bt_alram_play.setOnClickListener {
            startActivity(Intent(this, AlarmTriggerActivity::class.java))
        }
    bt_share.setOnClickListener {
        /** ACTION_SEND: Deliver some data to someone else.
        createChooser (Intent target, CharSequence title): Here, target- The Intent that the user will be selecting an activity to perform.
        title- Optional title that will be displayed in the chooser.
        Intent.EXTRA_TEXT: A constant CharSequence that is associated with the Intent, used with ACTION_SEND to supply the literal data to be sent.
         */
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
            var shareMessage = "\nLet me recommend you this application\n\n"
            shareMessage = """
                ${shareMessage}https://play.google.com/store/apps/details?id=com.amartexlogistics
                
                
                """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "Share via "))
        } catch (e: Exception) {
            //e.toString();
        }

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
        /*if (requestCode == SCAN_DATA && data != null) {
            binding.events = data.getParcelableExtra("scan_data")
        }*/

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "error in  scanning", Toast.LENGTH_SHORT).show()
            return
        }
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        if (requestCode == BARCODE_READER_ACTIVITY_REQUEST && data != null) {
            val barcode: Barcode =
                data.getParcelableExtra(BarcodeReaderActivity.KEY_CAPTURED_BARCODE)
            Toast.makeText(this, barcode.rawValue, Toast.LENGTH_SHORT).show()
            BarcodeReaderFragment().playBeep()
        }
    }

    companion object {
        const val SCAN_DATA = 8000
    }
}
