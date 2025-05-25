package com.example.weather_report

import android.app.Application
import com.example.weather_report.utils.AppliedSystemSettings

class WeatherReport : Application() {
    override fun onCreate() {
        super.onCreate()
        AppliedSystemSettings.getInstance(this)
    }
}