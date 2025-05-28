package com.example.weather_report.contracts

interface HomeScreenContract {
    interface View {
        fun onSwipeToRefreshData()
        fun setupAdaptersAndRVs()
        fun setupObservers()
        fun updateWeatherUI()
        fun updateExtraInfo()
    }
}