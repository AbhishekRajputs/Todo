package com.example.todo.dialogFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R


class MyDialogFragment(private var itemClickListener: ItemClickListener) : DialogFragment(),
    DialogAdapter.ItemClickListener {

    private var categories = arrayOf(
        "Work",
        "Health",
        "Vacation",
        "BirthDay",
        "Cooking",
        "Payment",
        "Meeting"
    )
    private lateinit var rv: RecyclerView
    private lateinit var adapter: DialogAdapter

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.dialog_fragment, container, false)
        rv = rootView.findViewById(R.id.rv_category_items)
        rv.layoutManager = GridLayoutManager(
            this.activity, 2,
            GridLayoutManager.VERTICAL, false
        )
        this.dialog!!.setTitle("Select Category")
        adapter = DialogAdapter(categories, this)
        rv.adapter = adapter
        return rootView
    }

    override fun itemClick(value: String) {
        itemClickListener.itemClick(value)
    }

    interface ItemClickListener {
        fun itemClick(value: String)
    }
}