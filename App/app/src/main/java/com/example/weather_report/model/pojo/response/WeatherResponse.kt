package com.example.weather_report.model.pojo.response

import com.example.weather_report.model.pojo.sub.Clouds
import com.example.weather_report.model.pojo.sub.Coordinates
import com.example.weather_report.model.pojo.sub.MainWeather
import com.example.weather_report.model.pojo.sub.Sys
import com.example.weather_report.model.pojo.sub.Weather
import com.example.weather_report.model.pojo.sub.Wind

data class WeatherResponse(
    val coord: Coordinates,
    val weather: List<Weather>,
    val base: String,
    val main: MainWeather,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)