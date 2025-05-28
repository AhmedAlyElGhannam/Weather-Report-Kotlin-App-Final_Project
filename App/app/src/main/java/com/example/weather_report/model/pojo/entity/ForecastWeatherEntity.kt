package com.example.weather_report.model.pojo.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weather_report.model.pojo.response.ForecastResponse


@Entity(tableName = "forecast_weather")
data class ForecastWeatherEntity(
    @PrimaryKey val locationId: String,
    val forecastData: ForecastResponse,
    val lastUpdated: Long = System.currentTimeMillis()
)
