package com.example.weather_report.features.favlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather_report.model.repository.IWeatherRepository

class FavouriteLocationsViewModelFactory(private val repo : IWeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouriteLocationsViewModel(repo) as T
    }
}
