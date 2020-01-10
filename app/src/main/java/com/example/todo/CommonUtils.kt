package com.example.todo

import android.content.Context
import android.graphics.Bitmap
import android.transition.Slide
import android.view.Gravity
import android.view.Window
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.example.todo.modal.Events
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix


object CommonUtils {
    fun setAnimation(window: Window) {
        val slide = Slide()
        slide.slideEdge = Gravity.START
        slide.duration = 400
        slide.interpolator = DecelerateInterpolator()
        window.exitTransition = slide
        window.enterTransition = slide
    }

    fun toImageEncode(Value: Events, context: Context): Bitmap? {
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
                        context,
                        R.color.black
                    )
                    else ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                }
            }
           val bitmap =
                Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
            bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
    }

}