package com.example.todo.eventList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.example.todo.modal.Events
import com.example.todo.R
import kotlinx.android.synthetic.main.rv_event_item.view.*

class EventListAdapter(
     var events: ArrayList<Events>
) :
    RecyclerView.Adapter<EventListAdapter.EventsViewHolder>() {

    private lateinit var modifyItemListener :ModifyItemListener


    fun setListener(modifyItemListener: ModifyItemListener)
    {
        this.modifyItemListener = modifyItemListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        return EventsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.rv_event_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        holder.bindItems(events[position])
    }

    fun updateAdapter(list: List<Events>) {
        events = list as ArrayList<Events>
        notifyDataSetChanged()
    }

    inner class EventsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(events: Events) {
            val generator = ColorGenerator.MATERIAL
            val builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(1)
                .endConfig()
                .rect()

            itemView.img_event.setImageDrawable(
                builder.build(
                    events.eventName.substring(0, 1),
                    generator.randomColor
                )
            )
            itemView.img_edit.setOnClickListener {
                modifyItemListener.viewEvent(events)
            }
            itemView.tv_event_name.text = events.eventName
            itemView.tv_event_category.text = events.eventCategory
            itemView.tv_event_date_time.text = events.eventDate + "--" + events.eventTime
        }
    }

    interface ModifyItemListener {
        fun viewEvent(events: Events)
    }
}
