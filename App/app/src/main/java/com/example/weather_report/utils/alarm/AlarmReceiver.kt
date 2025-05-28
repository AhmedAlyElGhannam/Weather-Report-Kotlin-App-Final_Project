package com.example.weather_report.utils.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.example.weather_report.R
import com.example.weather_report.utils.settings.AppliedSystemSettings
import com.example.weather_report.utils.settings.notif.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        private var mediaPlayer: MediaPlayer? = null

        fun dismissAlarm(context: Context) {
            mediaPlayer?.release()
            mediaPlayer = null
            var appliedSystemSettings: AppliedSystemSettings = AppliedSystemSettings.getInstance(context)
            appliedSystemSettings.setIsAlarmActive(false)
            context.stopService(Intent(context, OverlayService::class.java))

        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { ctx ->
            if (!Settings.canDrawOverlays(ctx)) {
                val overlayIntent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${ctx.packageName}")
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                ctx.startActivity(overlayIntent)
                return
            }

            // Start media playback
            mediaPlayer = MediaPlayer.create(ctx, R.raw.birdland).apply {
                setOnCompletionListener { release() }
                start()
            }

            // Show notification
            NotificationHelper(ctx).showNotification(
                "Alarm",
                "Time's up!",
                soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            )

            // Start overlay service
            val overlayIntent = Intent(ctx, OverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ctx.startForegroundService(overlayIntent)
            } else {
                ctx.startService(overlayIntent)
            }
        }
    }
}