package com.example.weather_report.model.remote


interface IWeatherRemoteDataSource {
    suspend fun makeNetworkCall(lat : Double, lon : Double, units : String) : ForecastResponse?
}