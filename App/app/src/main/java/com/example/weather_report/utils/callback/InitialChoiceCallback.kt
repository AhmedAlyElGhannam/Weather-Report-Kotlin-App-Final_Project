package com.example.weather_report.utils.callback

interface InitialChoiceCallback {
    fun onGpsChosen()
    fun onMapChosen()
    fun onNotificationsEnabled()
}