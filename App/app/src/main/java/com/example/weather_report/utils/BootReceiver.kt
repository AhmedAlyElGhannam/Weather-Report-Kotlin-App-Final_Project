package com.example.weather_report.utils


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Here you would typically restore alarms from persistent storage
            Log.d("BootReceiver", "Device booted, should restore alarms here")
        }
    }
}