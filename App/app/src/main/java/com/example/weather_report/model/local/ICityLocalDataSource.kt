package com.example.weather_report.model.local

import com.example.weather_report.model.pojo.City


interface ICityLocalDataSource {
    suspend fun insertCity(city: City)
    suspend fun removeCity(city: City)
    suspend fun getAllCities(): List<City>
    suspend fun getCityByID(id : Int) : City?
    suspend fun updateCityInfo(city : City)
    suspend fun isFavouriteCity(id : Int) : Boolean
}