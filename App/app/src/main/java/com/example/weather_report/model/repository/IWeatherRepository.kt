package com.example.weather_report.model.repository

import com.example.weather_report.model.pojo.ForecastResponse
import com.example.weather_report.model.pojo.LocationWithWeather
import com.example.weather_report.model.pojo.WeatherResponse

interface IWeatherRepository {
    suspend fun fetchForecastDataRemotely(
        lat : Double,
        lon : Double,
        units : String,
        lang: String
    ) : ForecastResponse?

    suspend fun fetchCurrentWeatherDataRemotely(
        lat : Double,
        lon : Double,
        units : String,
        lang: String
    ) : WeatherResponse?

    suspend fun setCurrentLocation(lat: Double, lon: Double)
    suspend fun getCurrentLocationWithWeather(forceRefresh: Boolean, isNetworkAvailable: Boolean): LocationWithWeather?
    suspend fun getCurrentLocationId(): String?
    suspend fun addFavouriteLocation(lat: Double, lon: Double, name: String): Boolean
    suspend fun removeFavouriteLocation(locationId: String)
    suspend fun getFavouriteLocationsWithWeather(): List<LocationWithWeather>
    suspend fun refreshLocation(locationId: String): Boolean
    suspend fun deleteLocation(locationId: String)
    suspend fun setManualLocation(lat: Double, lon: Double, address: String)
    suspend fun getLocationWithWeather(locationId: String): LocationWithWeather?
}