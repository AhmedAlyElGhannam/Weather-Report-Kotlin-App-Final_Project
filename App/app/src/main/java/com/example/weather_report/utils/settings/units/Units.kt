package com.example.weather_report.utils.settings.units

enum class Units (val symbol: String, val description: String) {
    METERS_PER_SECOND("m/s", "Meters per Second"),
    MILES_PER_HOUR("mph", "Miles per Hour"),
    KILOMETERS_PER_HOUR("km/h", "Kilometers per Hour"),
    FEET_PER_SECOND("ft/s", "Feet per Second"),

    CELSIUS("C", "Celsius"),
    FAHRENHEIT("F", "Fahrenheit"),
    KELVIN("K", "Kelvin"),

    HECTOPASCAL("hPa", "Hectopascal"),
    PSI("Psi", "Pounds per Square Inch"),
    BAR("Bar", "Bar"),
    ATMOSPHERE("atm", "Atmosphere"),

    METER("m", "meters"),
    KILOMETER("km", "kilometers"),
    MILE("mi", "miles"),
    FEET("ft", "feet");
}