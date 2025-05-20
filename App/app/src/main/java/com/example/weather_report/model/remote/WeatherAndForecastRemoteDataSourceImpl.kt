package com.example.weather_report.model.remote

import com.example.weather_report.model.pojo.ForecastResponse
import com.example.weather_report.model.pojo.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherAndForecastRemoteDataSourceImpl(var IWeatherService: IWeatherService)
    : IWeatherAndForecastRemoteDataSource {
    override suspend fun makeNetworkCallToGetForecast(lat : Double, lon : Double, units : String) : ForecastResponse? {
        return withContext(Dispatchers.IO) {
            IWeatherService.getForecast(
                lat,
                lon,
                units
            )?.body()
        }
    }

    override suspend fun makeNetworkCallToGetCurrentWeather(
        lat : Double,
        lon : Double,
        units : String
    ) : WeatherResponse? {
        return withContext(Dispatchers.IO) {
            IWeatherService.getWeather(
                lat,
                lon,
                units
            )?.body()
        }
    }
}