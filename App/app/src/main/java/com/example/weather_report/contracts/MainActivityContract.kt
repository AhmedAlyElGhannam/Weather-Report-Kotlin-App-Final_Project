package com.example.weather_report.contracts

interface MainActivityContract {
    interface View {
        fun initializeNavigationDrawer()
        fun getFreshLocation()
    }
    interface ViewModel {
        fun fetchWeatherData(isConnected: Boolean, lat: Double, lon: Double)
        fun fetchForecastData(isConnected: Boolean, lat: Double, lon: Double)
        fun loadLocalWeatherData()
        fun loadLocalForecastData()
    }
    interface Model {

    }
}