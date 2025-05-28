package com.example.weather_report.contracts

import com.example.weather_report.model.pojo.entity.LocationWithWeather

interface WeatherDetailsContract {
    interface View {
        fun setupAdaptersAndRVs()
        fun setupObservers()
        fun updateWeatherUI()
        fun updateExtraInfo()
        fun onSwipeToRefreshData()
    }
    interface ViewModel {
        fun setFavoriteLocationData(locationWithWeather: LocationWithWeather)
        fun refreshLocationData()
    }
    interface Model {

    }
}