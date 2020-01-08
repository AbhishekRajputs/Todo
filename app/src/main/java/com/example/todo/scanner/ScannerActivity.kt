package com.example.todo.scanner

import android.Manifest.permission.CAMERA
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.todo.R
import com.example.todo.addEvent.AddEventActivity.Companion.SCAN_DATA
import com.example.todo.modal.Events
import com.google.gson.Gson
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import timber.log.Timber


class ScannerActivity : Activity(), ZXingScannerView.ResultHandler {

    private val scannerView by lazy {
        ZXingScannerView(this)
    }

    companion object {
        private const val REQUEST_CAMERA = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(scannerView)
        val currentapiVersion = Build.VERSION.SDK_INT
        if (currentapiVersion >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(
                    applicationContext,
                    "Permission already granted",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                requestPermission()
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(CAMERA), REQUEST_CAMERA)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CAMERA -> if (grantResults.isNotEmpty()) {
                val cameraAccepted =
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (cameraAccepted) {
                    Toast.makeText(
                        applicationContext,
                        "Permission Granted, Now you can access camera",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Permission Denied, You cannot access the camera",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(CAMERA)) {
                            showMessageOKCancel("You need to allow the permissions",
                                DialogInterface.OnClickListener { _, _ ->
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(
                                            arrayOf(CAMERA),
                                            REQUEST_CAMERA
                                        )
                                    }
                                })
                            return
                        }
                    }
                }
            }
        }
    }


    private fun showMessageOKCancel(
        message: String,
        okListener: DialogInterface.OnClickListener
    ) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    override fun handleResult(rawResult: Result?) {
        val events = Gson().fromJson(rawResult!!.text, Events::class.java)
        val intent = Intent()
        intent.putExtra("scan_data", events)
        setResult(SCAN_DATA, intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                scannerView.setResultHandler(this)
                scannerView.startCamera()
            } else {
                requestPermission()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scannerView.stopCamera()
    }
}

