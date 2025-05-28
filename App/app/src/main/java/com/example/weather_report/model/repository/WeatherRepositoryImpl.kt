package com.example.weather_report.model.repository

import com.example.weather_report.contracts.FavouriteLocationsContract
import com.example.weather_report.model.local.ILocalDataSource
import com.example.weather_report.model.pojo.ForecastResponse
import com.example.weather_report.model.pojo.LocationEntity
import com.example.weather_report.model.pojo.LocationWithWeather
import com.example.weather_report.model.pojo.WeatherResponse
import com.example.weather_report.model.remote.IWeatherAndForecastRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class WeatherRepositoryImpl private constructor(
    private val remoteDataSource: IWeatherAndForecastRemoteDataSource,
    private val localDataSource: ILocalDataSource
)
    : IWeatherRepository,
        FavouriteLocationsContract.Model{

    companion object {
        @Volatile
        private var instance: WeatherRepositoryImpl? = null

        fun getInstance(
            remoteDataSource: IWeatherAndForecastRemoteDataSource,
            localDataSource: ILocalDataSource
        ): WeatherRepositoryImpl {
            return instance ?: synchronized(this) {
                instance ?: WeatherRepositoryImpl(remoteDataSource, localDataSource).also {
                    instance = it
                }
            }
        }
    }

    private var preferredUnits: String = "metric"
    private var manualLocation: Pair<Double, Double>? = null
    private var manualAddress: String? = null
    private var locationMethod: String = "auto"

    override suspend fun fetchCurrentWeatherDataRemotely(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): WeatherResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response = remoteDataSource.makeNetworkCallToGetCurrentWeather(lat, lon, units, lang)
                response?.let {
                    setCurrentLocation(lat, lon)
                    saveCurrentWeather(it)
                }
                response
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override suspend fun fetchForecastDataRemotely(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): ForecastResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response = remoteDataSource.makeNetworkCallToGetForecast(lat, lon, units, lang)
                response?.let {
                    setCurrentLocation(lat, lon)
                    saveForecast(it)
                }
                response
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private suspend fun saveCurrentWeather(weatherResponse: WeatherResponse) {
        val locationId = getCurrentLocationId() ?: return
        localDataSource.saveCurrentWeather(locationId, weatherResponse)
    }

    private suspend fun saveForecast(forecastResponse: ForecastResponse) {
        val locationId = getCurrentLocationId() ?: return
        localDataSource.saveForecast(locationId, forecastResponse)
    }

    override suspend fun setCurrentLocation(lat: Double, lon: Double) {
        withContext(Dispatchers.IO) {
            localDataSource.clearCurrentLocationFlag()
            val existingLocation = localDataSource.findLocationByCoordinates(lat, lon)
            if (existingLocation != null) {
                localDataSource.setCurrentLocation(existingLocation.id)
            } else {
                val newLocation = LocationEntity(
                    id = UUID.randomUUID().toString(),
                    name = "Current Location",
                    latitude = lat,
                    longitude = lon,
                    isCurrent = true
                )
                localDataSource.saveLocation(newLocation)
            }
        }
    }

    override suspend fun getCurrentLocationWithWeather(
        forceRefresh: Boolean,
        isNetworkAvailable: Boolean
    ): LocationWithWeather? {
        return withContext(Dispatchers.IO) {
            val currentLocation = localDataSource.getCurrentLocation() ?: return@withContext null
            val locationId = currentLocation.id
            val isStale = localDataSource.isWeatherDataStale(locationId)
            if ((forceRefresh || isStale) && isNetworkAvailable) {
                refreshLocationData(locationId)
            }
            localDataSource.getLocationWithWeather(locationId)
        }
    }

    override suspend fun getCurrentLocationId(): String? {
        return withContext(Dispatchers.IO) {
            localDataSource.getCurrentLocation()?.id
        }
    }

    override suspend fun addFavouriteLocation(lat: Double, lon: Double, name: String): Boolean {
        return withContext(Dispatchers.IO) {
            val existingLocation = localDataSource.findLocationByCoordinates(lat, lon)
            val locationId = if (existingLocation != null) {
                localDataSource.setFavouriteStatus(existingLocation.id, true)
                localDataSource.updateLocationName(existingLocation.id, name)
                existingLocation.id
            } else {
                val newLocation = LocationEntity(
                    id = UUID.randomUUID().toString(),
                    name = name, // Use the provided name
                    latitude = lat,
                    longitude = lon,
                    isFavourite = true
                )
                localDataSource.saveLocation(newLocation)
                newLocation.id
            }

            try {
                val weather = remoteDataSource.makeNetworkCallToGetCurrentWeather(
                    lat, lon,
                    getPreferredUnits(),
                    "en"
                )
                val forecast = remoteDataSource.makeNetworkCallToGetForecast(
                    lat, lon,
                    getPreferredUnits(),
                    "en"
                )

                weather?.let { localDataSource.saveCurrentWeather(locationId, it) }
                forecast?.let { localDataSource.saveForecast(locationId, it) }

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    override suspend fun removeFavouriteLocation(locationId: String) {
        withContext(Dispatchers.IO) {
            localDataSource.setFavouriteStatus(locationId, false)
        }
    }

    override suspend fun getFavouriteLocationsWithWeather(): List<LocationWithWeather> {
        return withContext(Dispatchers.IO) {
            localDataSource.getFavouriteLocationsWithWeather()
        }
    }

    override suspend fun refreshLocation(locationId: String): Boolean {
        return withContext(Dispatchers.IO) {
            val location = localDataSource.getLocation(locationId) ?: return@withContext false
            refreshLocationData(locationId)
            true
        }
    }

    override suspend fun deleteLocation(locationId: String) {
        withContext(Dispatchers.IO) {
            localDataSource.deleteLocation(locationId)
        }
    }

    override fun getPreferredUnits(): String {
        return preferredUnits
    }

    override fun getManualLocation(): Pair<Double, Double>? {
        return manualLocation
    }

    override fun getLocationMethod(): String {
        return locationMethod
    }

    override suspend fun setManualLocation(lat: Double, lon: Double, address: String) {
        withContext(Dispatchers.IO) {
            manualLocation = Pair(lat, lon)
            manualAddress = address
            locationMethod = "manual"
            setCurrentLocation(lat, lon)
            val existingLocation = localDataSource.findLocationByCoordinates(lat, lon)
            existingLocation?.let {
                localDataSource.updateLocationAddress(it.id, address)
            }
        }
    }

    override fun getManualLocationWithAddress(): Triple<Double, Double, String>? {
        return manualLocation?.let { (lat, lon) ->
            manualAddress?.let { address ->
                Triple(lat, lon, address)
            }
        }
    }

    override suspend fun getLocationWithWeather(locationId: String): LocationWithWeather? {
        return withContext(Dispatchers.IO) {
            localDataSource.getLocationWithWeather(locationId)
        }
    }

    private suspend fun refreshLocationData(locationId: String) {
        val location = localDataSource.getLocation(locationId) ?: return
        try {
            val weatherResponse = remoteDataSource.makeNetworkCallToGetCurrentWeather(
                location.latitude,
                location.longitude,
                getPreferredUnits(),
                "en"
            )
            val forecastResponse = remoteDataSource.makeNetworkCallToGetForecast(
                location.latitude,
                location.longitude,
                getPreferredUnits(),
                "en" 
            )
            weatherResponse?.let {
                localDataSource.saveCurrentWeather(locationId, it)
            }
            forecastResponse?.let {
                localDataSource.saveForecast(locationId, it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}