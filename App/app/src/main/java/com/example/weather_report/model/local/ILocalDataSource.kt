package com.example.weather_report.model.local

import com.example.weather_report.model.pojo.entity.CurrentWeatherEntity
import com.example.weather_report.model.pojo.response.ForecastResponse
import com.example.weather_report.model.pojo.entity.LocationEntity
import com.example.weather_report.model.pojo.entity.LocationWithWeather
import com.example.weather_report.model.pojo.response.WeatherResponse


interface ILocalDataSource {
    suspend fun saveLocation(location: LocationEntity)
    suspend fun getLocation(id: String): LocationEntity?
    suspend fun findLocationByCoordinates(lat: Double, lon: Double): LocationEntity?
    suspend fun getCurrentLocation(): LocationEntity?
    suspend fun getFavouriteLocations(): List<LocationEntity>
    suspend fun clearCurrentLocationFlag()
    suspend fun setCurrentLocation(locationId: String)
    suspend fun setFavouriteStatus(locationId: String, isFavourite: Boolean)
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
    suspend fun getFavouriteLocationsWithWeather(): List<LocationWithWeather>
    suspend fun updateLocationAddress(locationId: String, address: String)

    suspend fun isWeatherDataStale(locationId: String): Boolean
}