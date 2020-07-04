package com.example.todo.barCode

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.system.Os.close
import android.util.SparseArray
import androidx.appcompat.app.AppCompatActivity
import com.example.todo.R
import com.google.android.gms.vision.barcode.Barcode

class BarcodeReaderActivity : AppCompatActivity(), BarcodeReaderFragment.BarcodeReaderListener {
    private var autoFocus = false
    private var useFlash = false
    private var mBarcodeReaderFragment: BarcodeReaderFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_reader)
        val intent = intent
        if (intent != null) {
            autoFocus =
                intent.getBooleanExtra(KEY_AUTO_FOCUS, false)
            useFlash = intent.getBooleanExtra(KEY_USE_FLASH, false)
        }
        mBarcodeReaderFragment = attachBarcodeReaderFragment()
    }

    private fun attachBarcodeReaderFragment(): BarcodeReaderFragment {
        val supportFragmentManager =
            supportFragmentManager
        val fragmentTransaction =
            supportFragmentManager.beginTransaction()
        val fragment = BarcodeReaderFragment.newInstance(autoFocus, useFlash)
        fragment.setListener(this)
        fragmentTransaction.replace(R.id.fm_container, fragment)
        fragmentTransaction.commitAllowingStateLoss()
        return fragment
    }

    override fun onScanned(barcode: Barcode?) {
        if (mBarcodeReaderFragment != null) {
            mBarcodeReaderFragment!!.pauseScanning()
        }
        if (barcode != null) {
            val intent = Intent()
            intent.putExtra(KEY_CAPTURED_BARCODE, barcode)
            intent.putExtra(
                KEY_CAPTURED_RAW_BARCODE,
                barcode.calendarEvent
            )
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }

    override fun onScannedMultiple(barcodes: List<Barcode>?) {}

    override fun onBitmapScanned(sparseArray: SparseArray<Barcode?>?) {}

    override fun onScanError(errorMessage: String?) {}

    override fun onCameraPermissionDenied() {}
    override fun onPointerCaptureChanged(hasCapture: Boolean) {}

    companion object {
        var KEY_CAPTURED_BARCODE = "key_captured_barcode"
        var KEY_CAPTURED_RAW_BARCODE = "key_captured_raw_barcode"
        private const val KEY_AUTO_FOCUS = "key_auto_focus"
        private const val KEY_USE_FLASH = "key_use_flash"
        fun getLaunchIntent(
            context: Context?,
            autoFocus: Boolean,
            useFlash: Boolean
        ): Intent {
            val intent = Intent(context, BarcodeReaderActivity::class.java)
            intent.putExtra(KEY_AUTO_FOCUS, autoFocus)
            intent.putExtra(KEY_USE_FLASH, useFlash)
            return intent
        }
    }

}