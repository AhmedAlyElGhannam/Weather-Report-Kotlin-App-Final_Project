package com.example.mvvm.model.local

import com.example.weather_report.model.pojo.ForecastItem


interface IForecastItemLocalDataSource {
    suspend fun insertForecastItem(forecastItem: ForecastItem)
    suspend fun removeForecastItem(forecastItem: ForecastItem)
    suspend fun getAllForecastItems(): List<ForecastItem>
}