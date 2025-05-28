package com.example.weather_report.model.pojo.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weather_report.model.pojo.response.WeatherResponse

@Entity(tableName = "current_weather")
data class CurrentWeatherEntity(
    @PrimaryKey val locationId: String,
    val weatherData: WeatherResponse,
    val lastUpdated: Long = System.currentTimeMillis()
)
