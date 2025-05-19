package com.example.weather_report.model.repository

import com.example.weather_report.model.pojo.City
import com.example.weather_report.model.pojo.ForecastItem
import com.example.weather_report.model.remote.WeatherResponse

interface IWeatherRepository {
    suspend fun fetchWeatherDataRemotely(
        lat : Double,
        lon : Double,
        units : String
    ) : WeatherResponse?

    suspend fun fetchFavouriteCitiesLocally() : List<City>

    suspend fun deleteCityFromFavourites(city : City)

    suspend fun addCityToFavourites(city : City)

    suspend fun fetchAllSavedLocationForecastDataLocally() : List<ForecastItem>

    suspend fun deleteAllSavedLocationForecastData()

    suspend fun saveLocationForecastData(forecastItems : List<ForecastItem>)

    suspend fun saveLocationSingleForecastData(forecastItem: ForecastItem)

    suspend fun deleteLocationSingleForecastData(forecastItem: ForecastItem)
}