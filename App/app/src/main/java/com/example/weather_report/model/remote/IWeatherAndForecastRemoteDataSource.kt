package com.example.weather_report.model.remote


interface IWeatherAndForecastRemoteDataSource {
    suspend fun makeNetworkCallToGetForecast(lat : Double, lon : Double, units : String) : ForecastResponse?
    suspend fun makeNetworkCallToGetCurrentWeather(lat : Double, lon : Double, units : String) : WeatherResponse?
}