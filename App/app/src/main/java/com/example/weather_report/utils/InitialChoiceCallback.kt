package com.example.weather_report.utils

interface InitialChoiceCallback {
    fun onGpsChosen()
    fun onMapChosen()
    fun onNotificationsEnabled()
}