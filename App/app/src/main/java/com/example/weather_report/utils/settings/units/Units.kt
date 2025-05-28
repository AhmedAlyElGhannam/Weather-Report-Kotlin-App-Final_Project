package com.example.weather_report.utils.settings.units

import android.content.Context
import androidx.annotation.StringRes
import com.example.weather_report.R

enum class Units(@StringRes val symbolResId: Int, val description: String) {
    METERS_PER_SECOND(R.string.m_s, "Meters per Second"),
    MILES_PER_HOUR(R.string.mph, "Miles per Hour"),
    KILOMETERS_PER_HOUR(R.string.km_h, "Kilometers per Hour"),
    FEET_PER_SECOND(R.string.ft_s, "Feet per Second"),

    CELSIUS(R.string.celsius, "Celsius"),
    FAHRENHEIT(R.string.fahrenheit, "Fahrenheit"),
    KELVIN(R.string.kelvin, "Kelvin"),

    HECTOPASCAL(R.string.hpa, "Hectopascal"),
    PSI(R.string.psi, "Pounds per Square Inch"),
    BAR(R.string.bar, "Bar"),
    ATMOSPHERE(R.string.atm, "Atmosphere");

    fun getLocalizedSymbol(context: Context): String {
        return context.getString(symbolResId)
    }
}
