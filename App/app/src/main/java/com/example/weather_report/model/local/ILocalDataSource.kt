package com.example.weather_report.model.local

import com.example.weather_report.model.pojo.CurrentWeatherEntity
import com.example.weather_report.model.pojo.ForecastResponse
import com.example.weather_report.model.pojo.LocationEntity
import com.example.weather_report.model.pojo.LocationWithWeather
import com.example.weather_report.model.pojo.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


interface ILocalDataSource {
    suspend fun saveLocation(location: LocationEntity)
    suspend fun getLocation(id: String): LocationEntity?
    suspend fun findLocationByCoordinates(lat: Double, lon: Double): LocationEntity?
    suspend fun getCurrentLocation(): LocationEntity?
    suspend fun getFavoriteLocations(): List<LocationEntity>
    suspend fun clearCurrentLocationFlag()
    suspend fun setCurrentLocation(locationId: String)
    suspend fun setFavoriteStatus(locationId: String, isFavorite: Boolean)
    suspend fun updateLocationName(locationId: String, name: String)
    suspend fun deleteLocation(locationId: String)
    suspend fun saveCurrentWeather(locationId: String, weather: WeatherResponse)
    suspend fun getCurrentWeather(locationId: String): WeatherResponse?
    suspend fun saveForecast(locationId: String, forecast: ForecastResponse)
    suspend fun getForecast(locationId: String): ForecastResponse?
    suspend fun getLocationWithWeather(locationId: String): LocationWithWeather?
    suspend fun deleteCurrentWeather(locationId: String)
    suspend fun deleteForecast(locationId: String)
    suspend fun deleteCurrentWeather(currentWeather: CurrentWeatherEntity)
    suspend fun deleteStaleWeather(threshold: Long)
    suspend fun getFavoriteLocationsWithWeather(): List<LocationWithWeather>
    suspend fun updateLocationAddress(locationId: String, address: String)

    suspend fun isWeatherDataStale(locationId: String): Boolean
}