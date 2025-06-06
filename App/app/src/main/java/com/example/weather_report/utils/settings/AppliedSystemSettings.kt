package com.example.weather_report.utils.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.weather_report.utils.settings.lang.AvailableLanguages
import com.example.weather_report.utils.settings.loc.LocationOptions
import com.example.weather_report.utils.settings.notif.NotificationsOptions
import com.example.weather_report.utils.settings.units.UnitSystem
import com.example.weather_report.utils.settings.units.UnitSystemsConversions
import com.example.weather_report.utils.settings.units.Units
import kotlin.math.roundToInt

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

    fun setIsAlarmActive(stat: Boolean) {
        prefs.edit { putBoolean("alarm_active", stat) }
    }

    fun isAlarmActive(): Boolean {
        return prefs.getBoolean("alarm_active", false)
    }

    fun getIsInitialSetup(): Boolean {
        val value = prefs.getBoolean("initial_setup", true)
        return value
    }

    fun setIsInitialSetup() {
        prefs.edit { putBoolean("initial_setup", false) }
        prefs.edit { putBoolean("alarm_active", false) }
        setUnitSystem(UnitSystem.STANDARD)
        setLanguage(AvailableLanguages.ENGLISH)
        setTempUnit(Units.CELSIUS)
        setPressureUnit(Units.HECTOPASCAL)
        setSpeedUnit(Units.METERS_PER_SECOND)
    }

    fun getUnitSystem(): UnitSystem {
        val value = prefs.getString("unit_system", UnitSystem.CUSTOM.code) ?: UnitSystem.CUSTOM.code
        return UnitSystem.entries.find { it.code == value } ?: UnitSystem.CUSTOM
    }

    fun setUnitSystem(value: UnitSystem) {
        prefs.edit { putString("unit_system", value.code) }
    }

    fun getSpeedUnit(): Units {
        val value = prefs.getString("speed_unit", Units.METERS_PER_SECOND.name) ?: Units.METERS_PER_SECOND.name
        return Units.entries.find { it.name == value } ?: Units.METERS_PER_SECOND
    }

    fun setSpeedUnit(value: Units) {
        prefs.edit { putString("speed_unit", value.name) }
    }

    fun getTempUnit(): Units {
        val value = prefs.getString("temp_unit", Units.CELSIUS.name) ?: Units.CELSIUS.name
        return Units.entries.find { it.name == value } ?: Units.CELSIUS
    }

    fun setTempUnit(value: Units) {
        prefs.edit { putString("temp_unit", value.name) }
    }

    fun getPressureUnit(): Units {
        val value = prefs.getString("pressure_unit", Units.HECTOPASCAL.name) ?: Units.HECTOPASCAL.name
        return Units.entries.find { it.name == value } ?: Units.HECTOPASCAL
    }

    fun setPressureUnit(value: Units) {
        prefs.edit { putString("pressure_unit", value.name) }
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

    fun convertToTempUnit(tempKelvin: Double): Int {
        return when (getTempUnit().name) {
            Units.CELSIUS.name -> {
                UnitSystemsConversions.kelvinToCelsius(tempKelvin).roundToInt()
            }
            Units.FAHRENHEIT.name -> {
                UnitSystemsConversions.kelvinToFahrenheit(tempKelvin).roundToInt()
            }
            else -> {
                tempKelvin.roundToInt()
            }
        }
    }

    fun convertToSpeedUnit(speed: Double): Int {
        return when (getSpeedUnit().name) {
            Units.KILOMETERS_PER_HOUR.name -> {
                setSpeedUnit(Units.KILOMETERS_PER_HOUR)
                UnitSystemsConversions.meterPerSecondToKilometerPerHour(speed).roundToInt()
            }
            Units.MILES_PER_HOUR.name -> {
                setSpeedUnit(Units.MILES_PER_HOUR)
                UnitSystemsConversions.meterPerSecondToMilePerHour(speed).roundToInt()
            }
            Units.FEET_PER_SECOND.name -> {
                setSpeedUnit(Units.FEET_PER_SECOND)
                UnitSystemsConversions.meterPerSecondToFeetPerSecond(speed).roundToInt()
            }
            else -> {
                speed.roundToInt()
            }
        }
    }

    fun convertToPressureUnit(pressure: Double): Int {
        return when (getPressureUnit().name) {
            Units.PSI.name -> {
                setPressureUnit(Units.PSI)
                UnitSystemsConversions.hectopascalToPsi(pressure).roundToInt()
            }
            Units.ATMOSPHERE.name -> {
                setPressureUnit(Units.ATMOSPHERE)
                UnitSystemsConversions.hectopascalToAtm(pressure).roundToInt()
            }
            Units.BAR.name -> {
                setPressureUnit(Units.BAR)
                UnitSystemsConversions.hectopascalToBar(pressure).roundToInt()
            }
            else -> {
                pressure.roundToInt()
            }
        }
    }
}