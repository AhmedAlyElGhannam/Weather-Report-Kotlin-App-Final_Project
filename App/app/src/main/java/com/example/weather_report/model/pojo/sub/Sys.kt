package com.example.weather_report.model.pojo.sub

data class Sys(
    val pod: String,
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)