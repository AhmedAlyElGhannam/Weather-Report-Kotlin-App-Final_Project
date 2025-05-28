package com.example.weather_report.features.favlist.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_report.contracts.FavouriteLocationsContract
import com.example.weather_report.model.pojo.LocationWithWeather
import com.example.weather_report.model.repository.IWeatherRepository
import kotlinx.coroutines.launch

class FavouriteLocationsViewModel(private val repo: IWeatherRepository)
    : ViewModel(), FavouriteLocationsContract.ViewModel {

    private val _favoriteLocations = MutableLiveData<List<LocationWithWeather>>()
    val favoriteLocations: LiveData<List<LocationWithWeather>> = _favoriteLocations

    override fun loadFavouriteLocations() {
        viewModelScope.launch {
            try {
                val favorites = repo.getFavouriteLocationsWithWeather()
                _favoriteLocations.postValue(favorites)
            } catch (e: Exception) {
                Log.i("TAG", "loadFavouriteLocations: could not get favourite locations with weather")
            }
        }
    }

    override fun addFavourite(lat: Double, lon: Double, name: String) {
        viewModelScope.launch {
            try {
                val res = repo.addFavouriteLocation(lat, lon, name)
                if (res) {
                    Log.i("TAG", "addFavourite: Success")
                    loadFavouriteLocations()
                } else {
                    Log.i("TAG", "addFavourite: Failure")
                }
            } catch (e: Exception) {
                Log.i("TAG", "addFavourite: could not add to favourites")
            }
        }
    }

    override fun removeFavourite(locationId: String) {
        viewModelScope.launch {
            try {
                repo.removeFavouriteLocation(locationId)
                loadFavouriteLocations()
            } catch (e: Exception) {
                Log.i("TAG", "removeFavourite: could not remove from favourites")
            }
        }
    }
}