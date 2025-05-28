package com.example.weather_report.utils.settings.units

enum class UnitSystem(val code: String, val desc: String) {
    STANDARD("standard", "SI Units"),
    IMPERIAL("imperial", "Imperial Units"),
    CUSTOM("metric", "Custom");
}