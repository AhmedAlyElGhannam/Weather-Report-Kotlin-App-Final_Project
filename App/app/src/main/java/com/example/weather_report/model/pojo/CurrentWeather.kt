package com.example.weather_report.model.pojo

data class CurrentWeather(
    val id: Int,
    val coord: Coordinates,
    val weather: List<Weather>,
    val base: String,
    val main: MainWeather,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val rain: Rain?,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val name: String,
    val cod: Int
)