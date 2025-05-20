package com.example.weather_report.model.local

import com.example.weather_report.model.pojo.ForecastItem


interface IForecastItemLocalDataSource {
    suspend fun insertForecastItem(forecastItem: ForecastItem)
    suspend fun removeForecastItem(forecastItem: ForecastItem)
    suspend fun getAllForecastItems(): List<ForecastItem>
    suspend fun insertAllForecastItems(forecastItems : List<ForecastItem>)
    suspend fun deleteAllForecastItems()
    suspend fun getForcastItemsByCityID(id : Int) : List<ForecastItem>
    suspend fun updateForecastItem(forecastItem: ForecastItem)
}