package com.example.weather_report.utils

enum class UnitSystem(val code: String, val desc: String) {
    STANDARD("standard", "SI Units"),
    IMPERIAL("imperial", "Imperial Units"),
    CUSTOM("metric", "Custom");
}