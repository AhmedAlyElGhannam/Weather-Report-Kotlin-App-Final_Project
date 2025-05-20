package com.example.weather_report.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Forecast_Table")
data class ForecastItem(
    @PrimaryKey val dt: Long,
    val main: MainWeather,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val sys: Sys,
    val dt_txt: String
)