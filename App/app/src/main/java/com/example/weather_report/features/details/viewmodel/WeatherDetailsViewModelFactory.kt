package com.example.weather_report.features.details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather_report.model.repository.IWeatherRepository

class WeatherDetailsViewModelFactory(private val repo : IWeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherDetailsViewModel(repo) as T
    }
}
