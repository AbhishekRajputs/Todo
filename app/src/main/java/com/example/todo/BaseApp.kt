package com.example.todo

import android.app.*
import timber.log.Timber

class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}