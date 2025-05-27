package com.example.weather_report.model.remote

import com.example.weather_report.model.pojo.ForecastResponse
import com.example.weather_report.model.pojo.WeatherResponse


interface IWeatherAndForecastRemoteDataSource {
    suspend fun makeNetworkCallToGetForecast(lat : Double, lon : Double, units : String, lang: String) : ForecastResponse?
    suspend fun makeNetworkCallToGetCurrentWeather(lat : Double, lon : Double, units : String, lang: String) : WeatherResponse?
}