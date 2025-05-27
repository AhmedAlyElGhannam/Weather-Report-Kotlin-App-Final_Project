package com.example.weather_report

interface IGPSCoordinatesCallback {
    fun onCoordinatesFetchedByGPS(lat: Double, lon: Double)
}