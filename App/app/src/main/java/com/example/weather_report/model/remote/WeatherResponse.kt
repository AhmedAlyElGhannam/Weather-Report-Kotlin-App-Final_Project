package com.example.weather_report.model.remote

import com.example.weather_report.model.pojo.Clouds
import com.example.weather_report.model.pojo.Coordinates
import com.example.weather_report.model.pojo.MainWeather
import com.example.weather_report.model.pojo.Rain
import com.example.weather_report.model.pojo.Sys
import com.example.weather_report.model.pojo.Weather
import com.example.weather_report.model.pojo.Wind

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