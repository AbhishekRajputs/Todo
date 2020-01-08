package com.example.todo.eventList

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.todo.addEvent.AddEventActivity
import com.example.todo.CommonUtils.setAnimation
import com.example.todo.modal.Events
import com.example.todo.R
import com.example.todo.viewEvent.ViewEventActivity.Companion.createIntent
import kotlinx.android.synthetic.main.activity_event_list.*

class EventListActivity : AppCompatActivity(), EventListAdapter.ModifyItemListener {

    private lateinit var adapter: EventListAdapter
    private val eventList = arrayListOf<Events>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        img_add_event.setOnClickListener {
            setAnimation(window)
            val options = ActivityOptions.makeSceneTransitionAnimation(this)
            startActivityForResult(
                Intent(this, AddEventActivity::class.java),
                EVENT_LIST,
                options.toBundle()
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EVENT_LIST && data != null) {
            val event = data.getParcelableExtra<Events>("event_data")
            eventList.add(event)
            adapter = EventListAdapter(eventList, this)
            rv_events.adapter = adapter
        }
    }


    companion object {
        const val EVENT_LIST = 7000
    }

    override fun editClickListener(events: Events) {
        startActivity(createIntent(this, events))
    }

    override fun deleteClickListener(adapterPosition: Int) {

    }
}
