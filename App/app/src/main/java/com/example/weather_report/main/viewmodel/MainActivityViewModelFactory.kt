package com.example.weather_report.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather_report.model.repository.IWeatherRepository

class MainActivityViewModelFactory(
    private val repo: IWeatherRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainActivityViewModel(repo) as T
    }
}
