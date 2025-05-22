package com.example.weather_report

interface InitialChoiceCallback {
    fun onGpsChosen()
    fun onMapChosen()
    fun onNotificationsEnabled()
}