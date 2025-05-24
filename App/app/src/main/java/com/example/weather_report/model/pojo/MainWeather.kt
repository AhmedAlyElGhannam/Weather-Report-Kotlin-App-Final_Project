package com.example.weather_report.model.pojo

data class MainWeather(
    var temp: Double,
    var feels_like: Double,
    var temp_min: Double,
    var temp_max: Double,
    var pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    var temp_kf: Double
)