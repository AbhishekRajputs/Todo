package com.example.todo.dialogFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import kotlinx.android.synthetic.main.rv_dialog_items.view.*

class DialogAdapter(
    private val list: Array<String>,
    private var itemClickListener: ItemClickListener
) :
    RecyclerView.Adapter<DialogAdapter.DialogViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogViewHolder {
        return DialogViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.rv_dialog_items,
                parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
        holder.bindItems(list[position])
    }


    inner class DialogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(value: String) {
            itemView.tv_category_name.text = value
            itemView.cardview_id.setOnClickListener {
                itemClickListener.itemClick(value)
            }
        }

    }

    interface ItemClickListener {
        fun itemClick(value: String)
    }
}
