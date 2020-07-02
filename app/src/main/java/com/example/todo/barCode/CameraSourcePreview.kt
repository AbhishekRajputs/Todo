package com.example.todo.barCode

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import java.io.IOException


class CameraSourcePreview(
    private val mContext: Context,
    attrs: AttributeSet?
) :
    ViewGroup(mContext, attrs) {
    private val mSurfaceView: SurfaceView
    private var mStartRequested = false
    private var mSurfaceAvailable = false
    private var mCameraSource: CameraSource? = null
    private var mOverlay: GraphicOverlay<*>? = null
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @RequiresPermission(Manifest.permission.CAMERA)
    @Throws(
        IOException::class,
        SecurityException::class
    )
    fun start(cameraSource: CameraSource?) {
        if (cameraSource == null) {
            stop()
        }
        mCameraSource = cameraSource
        if (mCameraSource != null) {
            mStartRequested = true
            startIfReady()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @RequiresPermission(Manifest.permission.CAMERA)
    @Throws(
        IOException::class,
        SecurityException::class
    )
    fun start(
        cameraSource: com.example.todo.barCode.CameraSource?,
        overlay: GraphicOverlay<BarcodeGraphic>?
    ) {
        mOverlay = overlay!!
        start(cameraSource)
    }

    fun stop() {
        if (mCameraSource != null) {
            mCameraSource!!.stop()
        }
    }

    fun release() {
        if (mCameraSource != null) {
            mCameraSource?.release()
            mCameraSource = null
        }
    }

    fun setFlash(flag: Boolean) {}

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @RequiresPermission(Manifest.permission.CAMERA)
    @Throws(
        IOException::class,
        SecurityException::class
    )
    private fun startIfReady() {
        if (mStartRequested && mSurfaceAvailable) {
            mCameraSource!!.start(mSurfaceView.holder)
            if (mOverlay != null) {
                val size = mCameraSource!!.previewSize
                val min = size!!.width.coerceAtMost(size.height)
                val max = size.width.coerceAtLeast(size.height)
                if (isPortraitMode) {
                    // Swap width and height sizes when in portrait, since it will be rotated by
                    // 90 degrees
                    mOverlay!!.setCameraInfo(min, max, mCameraSource!!.cameraFacing)
                } else {
                    mOverlay!!.setCameraInfo(max, min, mCameraSource!!.cameraFacing)
                }
                mOverlay!!.clear()
            }
            mStartRequested = false
        }
    }

    private inner class SurfaceCallback : SurfaceHolder.Callback {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun surfaceCreated(surface: SurfaceHolder) {
            mSurfaceAvailable = true
            try {
                startIfReady()
            } catch (se: SecurityException) {
                Log.e(
                    TAG,
                    "Do not have permission to start the camera",
                    se
                )
            } catch (e: IOException) {
                Log.e(
                    TAG,
                    "Could not start camera source.",
                    e
                )
            }
        }

        override fun surfaceDestroyed(surface: SurfaceHolder) {
            mSurfaceAvailable = false
        }

        override fun surfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
        }
    }

    //    Changes into this method
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        var previewWidth = 320
        var previewHeight = 240
        if (mCameraSource != null) {
            val size = mCameraSource!!.previewSize
            if (size != null) {
                previewWidth = size.width
                previewHeight = size.height
            }
        }

        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
        if (isPortraitMode) {
            val tmp = previewWidth
            previewWidth = previewHeight
            previewHeight = tmp
        }
        val viewWidth = right - left
        val viewHeight = bottom - top
        val childWidth: Int
        val childHeight: Int
        var childXOffset = 0
        var childYOffset = 0
        val widthRatio = viewWidth.toFloat() / previewWidth.toFloat()
        val heightRatio = viewHeight.toFloat() / previewHeight.toFloat()

        // To fill the view with the camera preview, while also preserving the correct aspect ratio,
        // it is usually necessary to slightly oversize the child and to crop off portions along one
        // of the dimensions.  We scale up based on the dimension requiring the most correction, and
        // compute a crop offset for the other dimension.
        if (widthRatio > heightRatio) {
            childWidth = viewWidth
            childHeight = (previewHeight.toFloat() * widthRatio).toInt()
            childYOffset = (childHeight - viewHeight) / 2
        } else {
            childWidth = (previewWidth.toFloat() * heightRatio).toInt()
            childHeight = viewHeight
            childXOffset = (childWidth - viewWidth) / 2
        }
        for (i in 0 until childCount) {
            // One dimension will be cropped.  We shift child over or up by this offset and adjust
            // the size to maintain the proper aspect ratio.
            getChildAt(i).layout(
                -1 * childXOffset, -1 * childYOffset,
                childWidth - childXOffset, childHeight - childYOffset
            )
        }
        try {
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                )
            ) {
                return
            }
            startIfReady()
        } catch (e: IOException) {
            Log.e(
                TAG,
                "Could not start camera source.",
                e
            )
        }
    }

    private val isPortraitMode: Boolean
        private get() {
            val orientation = mContext.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                return false
            }
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                return true
            }
            Log.d(
                TAG,
                "isPortraitMode returning false by default"
            )
            return false
        }

    companion object {
        private const val TAG = "CameraSourcePreview"
    }

    init {
        mSurfaceView = SurfaceView(mContext)
        mSurfaceView.holder.addCallback(SurfaceCallback())
        addView(mSurfaceView)
    }
}