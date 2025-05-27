package com.example.weather_report.features.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_report.model.pojo.ForecastResponse
import com.example.weather_report.model.pojo.LocationWithWeather
import com.example.weather_report.model.pojo.WeatherResponse
import com.example.weather_report.model.repository.IWeatherRepository
import com.example.weather_report.utils.AppliedSystemSettings
import com.example.weather_report.utils.UnitSystem
import com.example.weather_report.utils.UnitSystemsConversions
import com.example.weather_report.utils.Units
import kotlinx.coroutines.launch

class HomeScreenViewModel(private val repo: IWeatherRepository) : ViewModel() {
    private val _weatherResponse = MutableLiveData<WeatherResponse?>()
    val weatherResponse: LiveData<WeatherResponse?> = _weatherResponse

    private val _forecastResponse = MutableLiveData<ForecastResponse?>()
    val forecastResponse: LiveData<ForecastResponse?> = _forecastResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _selectedFavoriteLocation = MutableLiveData<LocationWithWeather?>()
    val selectedFavoriteLocation: LiveData<LocationWithWeather?> = _selectedFavoriteLocation

    // Modify setFavoriteLocationData
    fun setFavoriteLocationData(locationWithWeather: LocationWithWeather) {
        _selectedFavoriteLocation.value = locationWithWeather
    }

    fun clearSelectedFavorite() {
        _selectedFavoriteLocation.value = null
//        isShowingFavorite = false
    }

    fun setWeatherData(weather: WeatherResponse?, forecast: ForecastResponse?) {
        _weatherResponse.value = weather
        _forecastResponse.value = forecast
    }

    fun fetchWeatherData(isConnected: Boolean, lat: Double, lon: Double, unit: String, lang: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (isConnected) {
                    val response = repo.fetchCurrentWeatherDataRemotely(lat, lon, unit, lang)
                    if (response != null) {
                        _weatherResponse.postValue(response)
                    } else {
                        loadLocationDataFallback(lat, lon)
                        _errorMessage.postValue("Failed to fetch weather data, using cached data")
                    }
                } else {
                    loadLocationDataFallback(lat, lon)
                    _errorMessage.postValue("Offline mode: Using cached weather data")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error loading weather data: ${e.message}")
                loadLocationDataFallback(lat, lon)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun fetchForecastData(isConnected: Boolean, lat: Double, lon: Double, unit: String, lang: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (isConnected) {
                    val response = repo.fetchForecastDataRemotely(lat, lon, unit, lang)
                    if (response != null) {
                        _forecastResponse.postValue(response)
                    } else {
                        loadLocationDataFallback(lat, lon)
                        _errorMessage.postValue("Failed to fetch forecast data, using cached data")
                    }
                } else {
                    loadLocationDataFallback(lat, lon)
                    _errorMessage.postValue("Offline mode: Using cached forecast data")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error loading forecast data: ${e.message}")
                loadLocationDataFallback(lat, lon)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private suspend fun loadLocationDataFallback(lat: Double, lon: Double) {
        val locationId = repo.getCurrentLocationId()
        if (locationId != null) {
            val locationWithWeather = repo.getLocationWithWeather(locationId)
            locationWithWeather?.let {
                _weatherResponse.postValue(it.currentWeather)
                _forecastResponse.postValue(it.forecast)
            }
        }
    }

    fun refreshCurrentFavorite() {
        viewModelScope.launch {
            _selectedFavoriteLocation.value?.let { favorite ->
                try {
                    val refreshed = repo.refreshLocation(favorite.location.id)
                    if (refreshed) {
                        val updated = repo.getLocationWithWeather(favorite.location.id)
                        _selectedFavoriteLocation.postValue(updated)
                    }
                } catch (e: Exception) {
                    _errorMessage.postValue("Failed to refresh favorite location")
                }
            }
        }
    }

}