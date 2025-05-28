package com.example.weather_report.model.local

import com.example.weather_report.model.pojo.CurrentWeatherEntity
import com.example.weather_report.model.pojo.ForecastWeatherEntity
import com.example.weather_report.model.pojo.LocationEntity
import com.example.weather_report.model.pojo.LocationWithWeather
import com.example.weather_report.model.pojo.WeatherResponse
import com.example.weather_report.model.pojo.ForecastResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalDataSourceImpl(private val weatherDao: WeatherDao) : ILocalDataSource {

    companion object {
        private const val DATA_FRESHNESS_THRESHOLD = 30 * 60 * 1000 // 30 minutes in milliseconds
    }

    override suspend fun saveLocation(location: LocationEntity) = withContext(Dispatchers.IO) {
        weatherDao.insertLocation(location)
    }

    override suspend fun getLocation(id: String): LocationEntity? = withContext(Dispatchers.IO) {
        weatherDao.getLocation(id)
    }

    override suspend fun findLocationByCoordinates(lat: Double, lon: Double): LocationEntity? =
        withContext(Dispatchers.IO) {
            weatherDao.findNearbyLocation(lat, lon)
        }

    override suspend fun getCurrentLocation(): LocationEntity? = withContext(Dispatchers.IO) {
        weatherDao.getCurrentLocation()
    }

    override suspend fun getFavouriteLocations(): List<LocationEntity> = withContext(Dispatchers.IO) {
        weatherDao.getFavouriteLocations()
    }

    override suspend fun clearCurrentLocationFlag() = withContext(Dispatchers.IO) {
        weatherDao.clearCurrentLocationFlag()
    }

    override suspend fun setCurrentLocation(locationId: String) = withContext(Dispatchers.IO) {
        weatherDao.setCurrentLocation(locationId)
    }

    override suspend fun setFavouriteStatus(locationId: String, isFavourite: Boolean) =
        withContext(Dispatchers.IO) {
            weatherDao.setFavouriteStatus(locationId, isFavourite)
        }

    override suspend fun updateLocationName(locationId: String, name: String) =
        withContext(Dispatchers.IO) {
            weatherDao.updateLocationName(locationId, name)
        }

    override suspend fun updateLocationAddress(locationId: String, address: String) =
        withContext(Dispatchers.IO) {
            weatherDao.updateLocationAddress(locationId, address)
        }

    override suspend fun deleteLocation(locationId: String) = withContext(Dispatchers.IO) {
        // Delete associated weather data first to maintain referential integrity
        weatherDao.deleteCurrentWeather(locationId)
        weatherDao.deleteForecast(locationId)
        weatherDao.deleteLocation(locationId)
    }

    override suspend fun saveCurrentWeather(locationId: String, weather: WeatherResponse) =
        withContext(Dispatchers.IO) {
            // Fixed: Added lastUpdated timestamp to CurrentWeatherEntity
            weatherDao.insertCurrentWeather(
                CurrentWeatherEntity(
                    locationId = locationId,
                    weatherData = weather,
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }

    override suspend fun getCurrentWeather(locationId: String): WeatherResponse? =
        withContext(Dispatchers.IO) {
            weatherDao.getCurrentWeather(locationId)?.weatherData
        }

    override suspend fun saveForecast(locationId: String, forecast: ForecastResponse) =
        withContext(Dispatchers.IO) {
            weatherDao.insertForecast(ForecastWeatherEntity(locationId, forecast))
        }

    override suspend fun getForecast(locationId: String): ForecastResponse? =
        withContext(Dispatchers.IO) {
            weatherDao.getForecast(locationId)?.forecastData
        }

    override suspend fun getLocationWithWeather(locationId: String): LocationWithWeather? =
        withContext(Dispatchers.IO) {
            weatherDao.getLocationWithWeather(locationId)?.let { dbData ->
                LocationWithWeather(
                    location = dbData.location,
                    currentWeather = dbData.currentWeather?.weatherData,
                    forecast = dbData.forecast?.forecastData
                )
            }
        }

    override suspend fun deleteCurrentWeather(locationId: String) = withContext(Dispatchers.IO) {
        weatherDao.deleteCurrentWeather(locationId)
    }

    override suspend fun deleteForecast(locationId: String) = withContext(Dispatchers.IO) {
        weatherDao.deleteForecast(locationId)
    }

    override suspend fun deleteCurrentWeather(currentWeather: CurrentWeatherEntity) =
        withContext(Dispatchers.IO) {
            weatherDao.deleteCurrentWeather(currentWeather)
        }

    override suspend fun deleteStaleWeather(threshold: Long) = withContext(Dispatchers.IO) {
        weatherDao.deleteStaleWeather(threshold)
    }

    override suspend fun getFavouriteLocationsWithWeather(): List<LocationWithWeather> =
        withContext(Dispatchers.IO) {
            weatherDao.getFavouriteLocations().mapNotNull { location ->
                weatherDao.getLocationWithWeather(location.id)?.let { dbData ->
                    LocationWithWeather(
                        location = dbData.location,
                        currentWeather = dbData.currentWeather?.weatherData,
                        forecast = dbData.forecast?.forecastData
                    )
                }
            }
        }

    // Added: Check if weather data for a location is stale
    override suspend fun isWeatherDataStale(locationId: String): Boolean = withContext(Dispatchers.IO) {
        val lastUpdated = weatherDao.getWeatherLastUpdated(locationId)
        lastUpdated == null || (System.currentTimeMillis() - lastUpdated) > DATA_FRESHNESS_THRESHOLD
    }
}