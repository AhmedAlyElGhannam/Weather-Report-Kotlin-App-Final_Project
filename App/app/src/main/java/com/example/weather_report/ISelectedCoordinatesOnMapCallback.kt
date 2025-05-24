package com.example.weather_report

interface ISelectedCoordinatesOnMapCallback {
    fun onCoordinatesSelected(lat : Double, lon : Double)
}