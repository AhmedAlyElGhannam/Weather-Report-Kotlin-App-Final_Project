package com.example.mvvm.model.remote

import com.example.weather_report.model.remote.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRemoteDataSourceImpl(var IWeatherService: IWeatherService) : IWeatherRemoteDataSource {
    override suspend fun makeNetworkCall(lat : Double, lon : Double, units : String) : WeatherResponse? {
        return withContext(Dispatchers.IO) {
            IWeatherService.getWeather(
                lat,
                lon,
                units
            )?.body()
        }
    }
}