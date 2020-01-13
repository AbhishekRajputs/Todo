package com.example.todo.viewEvent

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.todo.CommonUtils.toImageEncode
import com.example.todo.R
import com.example.todo.databinding.ActivityViewEventBinding
import com.example.todo.modal.Events
import kotlinx.android.synthetic.main.activity_view_event.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class ViewEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_event)

        intent?.let {
            if (it.hasExtra(EXTRA_EVENT_DATA)) {
                binding.events = it.getParcelableExtra(EXTRA_EVENT_DATA)
            }
        }

        btn_generate_qrcode.setOnClickListener {
            CoroutineScope(Dispatchers.IO).async {
                img_qr_code.setImageBitmap(
                    toImageEncode(
                        binding.events as Events,
                        this@ViewEventActivity
                    )
                )
            }
        }
    }

    companion object {
        private const val EXTRA_EVENT_DATA = "extra_event_data"
        fun createIntent(context: Context, events: Events): Intent {
            val intent = Intent(context, ViewEventActivity::class.java)
            intent.putExtra(EXTRA_EVENT_DATA, events)
            return intent
        }
    }
}
