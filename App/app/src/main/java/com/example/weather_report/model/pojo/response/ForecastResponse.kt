package com.example.weather_report.model.pojo.response

import com.example.weather_report.model.pojo.sub.City
import com.example.weather_report.model.pojo.sub.ForecastItem

data class ForecastResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastItem>,
    val city: City
)