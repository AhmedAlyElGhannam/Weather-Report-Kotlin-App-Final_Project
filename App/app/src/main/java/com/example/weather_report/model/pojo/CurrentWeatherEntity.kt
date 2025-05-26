package com.example.weather_report.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

 @Entity(tableName = "current_weather")
data class CurrentWeatherEntity(
    @PrimaryKey val locationId: String,
    val weatherData: WeatherResponse,
    val lastUpdated: Long = System.currentTimeMillis()
)
