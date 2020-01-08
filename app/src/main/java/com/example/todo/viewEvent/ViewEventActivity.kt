package com.example.todo.viewEvent

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.todo.R
import com.example.todo.modal.Events
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import kotlinx.android.synthetic.main.activity_view_event.*

class ViewEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_event)

        if (intent != null && intent.hasExtra(EXTRA_EVENT_DATA)) {
            val events = intent.getParcelableExtra<Events>(EXTRA_EVENT_DATA)
            img_qr_code.setImageBitmap(toImageEncode(events))
            tv_event_name.text = events.eventName
            tv_category.text = events.eventCategory
            tv_date.text = events.eventDate
            tv_time.text = events.eventTime
        }
    }

    private fun toImageEncode(Value: Events): Bitmap? {
        val bitMatrix: BitMatrix = try {
            MultiFormatWriter().encode(
                Gson().toJson(Value),
                BarcodeFormat.QR_CODE,
                500, 500, null
            )
        } catch (e: IllegalArgumentException) {
            return null
        }
        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth
            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] = if (bitMatrix[x, y]
                ) ContextCompat.getColor(
                    applicationContext,
                    R.color.black
                )
                else ContextCompat.getColor(
                    applicationContext,
                    R.color.white
                )
            }
        }
        val bitmap =
            Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
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
