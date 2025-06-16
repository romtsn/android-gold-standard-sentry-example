package com.example.androidcustommetricsapp

import android.app.Application

class MyApp : Application() {
    companion object {
        var appStartTime: Long = 0
    }
    override fun onCreate() {
        appStartTime = System.currentTimeMillis()
        super.onCreate()
    }
} 