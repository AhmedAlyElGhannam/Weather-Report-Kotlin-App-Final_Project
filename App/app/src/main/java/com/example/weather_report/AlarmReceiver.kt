package com.example.weather_report

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Start your overlay service
        val overlayIntent = Intent(context, OverlayService::class.java)
        context?.startService(overlayIntent)

        // Show notification
        NotificationHelper(context!!).showNotification("Alarm", "Time's up!")
    }
}
