package com.example.weather_report.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
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

    fun getIsInitialSetup(): Boolean {
        val value = prefs.getBoolean("initial_setup", true)
        return value
    }

    fun setIsInitialSetup() {
        prefs.edit { putBoolean("initial_setup", false) }
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

    fun convertToTempUnit(temp: Double, isMain: Boolean): Int {
        if (isMain) {
            return when (getTempUnit().symbol) {
                Units.CELSIUS.symbol -> {
                    setTempUnit(Units.CELSIUS)
                    UnitSystemsConversions.kelvinToCelsius(temp).roundToInt()
                }
                Units.FAHRENHEIT.symbol -> {
                    setTempUnit(Units.FAHRENHEIT)
                    UnitSystemsConversions.kelvinToFahrenheit(temp).roundToInt()
                }
                else -> {
                    setTempUnit(Units.KELVIN)
                    temp.roundToInt()
                }
            }
        }
        else {
            return when (getTempUnit().symbol) {
                Units.FAHRENHEIT.symbol -> {
                    setTempUnit(Units.FAHRENHEIT)
                    UnitSystemsConversions.celsiusToFahrenheit(temp).roundToInt()
                }
                Units.KELVIN.symbol -> {
                    setTempUnit(Units.KELVIN)
                    UnitSystemsConversions.celsiusToKelvin(temp).roundToInt()
                }
                else -> {
                    setTempUnit(Units.CELSIUS)
                    temp.roundToInt()
                }
            }
        }
    }

    fun convertToSpeedUnit(speed: Double): Int {
        return when (getSpeedUnit().symbol) {
            Units.KILOMETERS_PER_HOUR.symbol -> {
                setSpeedUnit(Units.KILOMETERS_PER_HOUR)
                UnitSystemsConversions.meterPerSecondToKilometerPerHour(speed).roundToInt()
            }
            Units.MILES_PER_HOUR.symbol -> {
                setSpeedUnit(Units.MILES_PER_HOUR)
                UnitSystemsConversions.meterPerSecondToMilePerHour(speed).roundToInt()
            }
            Units.FEET_PER_SECOND.symbol -> {
                setSpeedUnit(Units.FEET_PER_SECOND)
                UnitSystemsConversions.meterPerSecondToFeetPerSecond(speed).roundToInt()
            }
            else -> {
                speed.roundToInt()
            }
        }
    }

    fun convertToPressureUnit(pressure: Double): Int {
        return when (getPressureUnit().symbol) {
            Units.PSI.symbol -> {
                setSpeedUnit(Units.PSI)
                UnitSystemsConversions.hectopascalToPsi(pressure).roundToInt()
            }
            Units.ATMOSPHERE.symbol -> {
                setSpeedUnit(Units.ATMOSPHERE)
                UnitSystemsConversions.hectopascalToAtm(pressure).roundToInt()
            }
            Units.BAR.symbol -> {
                setSpeedUnit(Units.BAR)
                UnitSystemsConversions.hectopascalToBar(pressure).roundToInt()
            }
            else -> {
                pressure.roundToInt()
            }
        }
    }
}