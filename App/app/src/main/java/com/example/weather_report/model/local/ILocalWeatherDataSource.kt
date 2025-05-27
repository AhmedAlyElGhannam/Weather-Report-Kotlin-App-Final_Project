package com.example.weather_report.model.local

import com.example.weather_report.model.pojo.ForecastResponse
import com.example.weather_report.model.pojo.WeatherResponse

interface ILocalWeatherDataSource {
    suspend fun saveWeatherResponse(cityId: Int, response: WeatherResponse)
    suspend fun getWeatherResponse(cityId: Int): WeatherResponse?
}