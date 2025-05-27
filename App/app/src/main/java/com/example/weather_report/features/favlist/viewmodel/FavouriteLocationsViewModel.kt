package com.example.weather_report.features.favlist.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_report.model.pojo.LocationWithWeather
import com.example.weather_report.model.repository.IWeatherRepository
import kotlinx.coroutines.launch

class FavouriteLocationsViewModel(private val repo: IWeatherRepository) : ViewModel() {

    private val _favoriteLocations = MutableLiveData<List<LocationWithWeather>>()
    val favoriteLocations: LiveData<List<LocationWithWeather>> = _favoriteLocations

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadFavoriteLocations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val favorites = repo.getFavoriteLocationsWithWeather()
                _favoriteLocations.postValue(favorites)
            } catch (e: Exception) {
                _errorMessage.postValue("Error loading favorites: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun addFavourite(lat: Double, lon: Double, name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = repo.addFavoriteLocation(lat, lon, name)
                if (res) {
                    Log.i("TAG", "addFavourite: Success")
                    loadFavoriteLocations()
                } else {
                    _errorMessage.postValue("Failed to add favorite location")
                    Log.i("TAG", "addFavourite: Failure")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error adding favorite: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun removeFavorite(locationId: String) {
        viewModelScope.launch {
            try {
                repo.removeFavoriteLocation(locationId)
                loadFavoriteLocations()
            } catch (e: Exception) {
                _errorMessage.postValue("Error removing favorite: ${e.message}")
            }
        }
    }
}