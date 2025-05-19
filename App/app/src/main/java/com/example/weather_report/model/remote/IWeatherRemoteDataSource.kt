package com.example.weather_report.model.remote

import com.example.weather_report.model.remote.WeatherResponse


interface IWeatherRemoteDataSource {
    suspend fun makeNetworkCall(lat : Double, lon : Double, units : String) : WeatherResponse?
}