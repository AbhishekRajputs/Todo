package com.example.todo.eventList

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.todo.R
import com.example.todo.addEvent.AddEventActivity
import com.example.todo.database.AppDatabase
import com.example.todo.modal.Events
import com.example.todo.viewEvent.ViewEventActivity
import kotlinx.android.synthetic.main.activity_event_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class EventListActivity : AppCompatActivity(), EventListAdapter.ModifyItemListener {

    private val eventList = arrayListOf<Events>()
    private lateinit var list: List<Events>

    private val database by lazy {
        AppDatabase(this)
    }

    private val eventListAdapter by lazy {
        EventListAdapter(ArrayList())
    }
    private lateinit var swipeController: SwipeController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)
        rv_events.adapter = eventListAdapter
        eventListAdapter.setListener(this)

        swipeController = SwipeController(object : SwipeControllerActions {
            override fun onEditClicked(position: Int) {
            }

            override fun onDeleteClicked(position: Int) {
                val event = eventListAdapter.events[position].eventTime
                CoroutineScope(Dispatchers.IO).async {
                    database.todoDao().delete(event)
                }
                eventListAdapter.events.removeAt(position)
                eventListAdapter.notifyItemRemoved(position)
                eventListAdapter.notifyItemRangeChanged(position, eventListAdapter.itemCount)
            }
        })

        rv_events.addItemDecoration(object : ItemDecoration() {
            override fun onDraw(
                c: Canvas,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                swipeController.onDraw(c)
            }
        })

        val itemTouchhelper = ItemTouchHelper(swipeController)
        itemTouchhelper.attachToRecyclerView(rv_events)

        CoroutineScope(Dispatchers.IO).async {
            list = database.todoDao().getAllEvents()
            eventListAdapter.updateAdapter(list)
        }

        img_add_event.setOnClickListener {
            val intent = Intent(this, AddEventActivity::class.java)
            intent.putParcelableArrayListExtra(EXTRA_ALL_EVENT, list as ArrayList)
            startActivityForResult(intent, EVENT_LIST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EVENT_LIST && data != null) {
            val event = data.getParcelableExtra<Events>("event_data")
            eventList.add(event)
            eventListAdapter.updateAdapter(eventList)
        }
    }

    companion object {
        const val EVENT_LIST = 7000
        const val EXTRA_ALL_EVENT = "extra_all_events"
    }

    override fun viewEvent(events: Events) {
        startActivity(ViewEventActivity.createIntent(this, events))
    }
}

