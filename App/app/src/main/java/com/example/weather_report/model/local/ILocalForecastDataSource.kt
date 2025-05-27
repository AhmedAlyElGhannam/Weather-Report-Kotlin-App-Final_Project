package com.example.weather_report.model.local

import com.example.weather_report.model.pojo.ForecastResponse

interface ILocalForecastDataSource {
    suspend fun saveForecastResponse(cityId: Int, response: ForecastResponse)
    suspend fun getForecastResponse(cityId: Int): ForecastResponse?
}