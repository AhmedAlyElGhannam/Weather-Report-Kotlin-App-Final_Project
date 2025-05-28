package com.example.weather_report.model.pojo.sub

data class City(
    val id: Int,
    val name: String,
    val coord: Coordinates,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long,
    var isCurrLocation : Boolean = false
)