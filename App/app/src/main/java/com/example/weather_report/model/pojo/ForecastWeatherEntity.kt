package com.example.weather_report.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "forecast_weather")
data class ForecastWeatherEntity(
    @PrimaryKey val locationId: String,
    val forecastData: ForecastResponse,
    val lastUpdated: Long = System.currentTimeMillis()
)
