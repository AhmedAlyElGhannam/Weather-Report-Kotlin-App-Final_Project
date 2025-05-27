package com.example.weather_report.model.pojo

data class ForecastItem(
    val dt: Long,
    val main: MainWeather,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val sys: Sys,
    val dt_txt: String,
    val cityId : Int
)