package com.example.mvvm.model.local

import com.example.weather_report.model.pojo.City


interface ICityLocalDataSource {
    suspend fun insertCity(city: City)
    suspend fun removeCity(city: City)
    suspend fun getAllCities(): List<City>
}