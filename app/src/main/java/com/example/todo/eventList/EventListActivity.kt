package com.example.todo.eventList

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
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
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


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
            val string = writeToString()
            createPdf(string)

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


    private fun writeToString(): String {
        val sb = StringBuilder()
        var count = 1
        for (u in list) {
            sb.append("${count++}: ${u.eventName}")
        }
        return sb.toString()
    }


    private fun createPdf(sometext: String) {
        // create a new document
        val document = PdfDocument()

        // crate a page description
        var pageInfo = PageInfo.Builder(300, 600, 1).create()

        // start a page
        var page = document.startPage(pageInfo)
        var canvas = page.canvas
        var paint = Paint()
        paint.color = Color.RED
        canvas.drawCircle(50f, 50f, 30f, paint)
        paint.color = Color.BLACK
        canvas.drawText(sometext, 80f, 50f, paint)
        //canvas.drawt
        // finish the page
        document.finishPage(page)
        // draw text on the graphics object of the page

        // Create Page 2
        pageInfo = PageInfo.Builder(300, 600, 2).create()
        page = document.startPage(pageInfo)
        canvas = page.canvas
        paint = Paint()
        paint.color = Color.BLUE
        canvas.drawCircle(100f, 100f, 100f, paint)
        document.finishPage(page)

        // write the document content
        val directory_path: String =
            Environment.getExternalStorageDirectory().getPath().toString() + "/mypdf/"
        val file = File(directory_path)
        if (!file.exists()) {
            file.mkdirs()
        }
        val targetPdf = directory_path + "test-2.pdf"
        val filePath = File(targetPdf)
        try {
            document.writeTo(FileOutputStream(filePath))
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Something wrong: $e", Toast.LENGTH_LONG).show()
        }

        // close the document
        document.close()
    }

}