package com.example.weather_report.utils

enum class UnitSystem(val value: String, val desc: String) {
    STANDARD("standard", "SI Units"),
    METRIC("metric", "Metric Units"),
    IMPERIAL("imperial", "Imperial Units"),
    CUSTOM("custom", "Custom");
}