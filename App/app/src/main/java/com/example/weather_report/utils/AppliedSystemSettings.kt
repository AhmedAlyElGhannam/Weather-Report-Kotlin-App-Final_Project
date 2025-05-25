package com.example.weather_report.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class AppliedSystemSettings private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var instance: AppliedSystemSettings? = null

        fun getInstance(context: Context): AppliedSystemSettings {
            return instance ?: synchronized(this) {
                instance ?: AppliedSystemSettings(context.applicationContext).also { instance = it }
            }
        }
    }

    fun getUnitSystem(): UnitSystem {
        val value = prefs.getString("unit_system", UnitSystem.CUSTOM.value) ?: UnitSystem.CUSTOM.value
        return UnitSystem.entries.find { it.value == value } ?: UnitSystem.CUSTOM
    }

    fun setUnitSystem(value: UnitSystem) {
        prefs.edit { putString("unit_system", value.value) }
    }

    fun getSpeedUnit(): Units {
        val value = prefs.getString("speed_unit", Units.METERS_PER_SECOND.symbol) ?: Units.METERS_PER_SECOND.symbol
        return Units.entries.find { it.symbol == value } ?: Units.METERS_PER_SECOND
    }

    fun setSpeedUnit(value: Units) {
        prefs.edit { putString("speed_unit", value.symbol) }
    }

    fun getTempUnit(): Units {
        val value = prefs.getString("temp_unit", Units.CELSIUS.symbol) ?: Units.CELSIUS.symbol
        return Units.entries.find { it.symbol == value } ?: Units.CELSIUS
    }

    fun setTempUnit(value: Units) {
        prefs.edit { putString("temp_unit", value.symbol) }
    }

    fun getPressureUnit(): Units {
        val value = prefs.getString("pressure_unit", Units.HECTOPASCAL.symbol) ?: Units.HECTOPASCAL.symbol
        return Units.entries.find { it.symbol == value } ?: Units.HECTOPASCAL
    }

    fun setPressureUnit(value: Units) {
        prefs.edit { putString("pressure_unit", value.symbol) }
    }

    fun getLanguage(): AvailableLanguages {
        val code = prefs.getString("language", AvailableLanguages.ENGLISH.code) ?: AvailableLanguages.ENGLISH.code
        return AvailableLanguages.entries.find { it.code == code } ?: AvailableLanguages.ENGLISH
    }

    fun setLanguage(value: AvailableLanguages) {
        prefs.edit { putString("language", value.code) }
    }

    fun getSelectedLocationOption(): LocationOptions {
        val desc = prefs.getString("location", LocationOptions.GPS.desc) ?: LocationOptions.GPS.desc
        return LocationOptions.entries.find { it.desc == desc } ?: LocationOptions.GPS
    }

    fun setSelectedLocationOption(value: LocationOptions) {
        prefs.edit { putString("location", value.desc) }
    }

    fun getSelectedNotificationOption(): NotificationsOptions {
        val desc = prefs.getString("notifications", NotificationsOptions.NOTIFICATIONS_ON.desc) ?: NotificationsOptions.NOTIFICATIONS_ON.desc
        return NotificationsOptions.entries.find { it.desc == desc } ?: NotificationsOptions.NOTIFICATIONS_ON
    }

    fun setSelectedNotificationOption(value: NotificationsOptions) {
        prefs.edit { putString("notifications", value.desc) }
    }
}