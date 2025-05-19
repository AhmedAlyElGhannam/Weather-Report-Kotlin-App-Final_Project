package com.example.weather_report.model.remote

import com.example.weather_report.model.pojo.City
import com.example.weather_report.model.pojo.ForecastItem

data class WeatherResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastItem>,
    val city: City
)