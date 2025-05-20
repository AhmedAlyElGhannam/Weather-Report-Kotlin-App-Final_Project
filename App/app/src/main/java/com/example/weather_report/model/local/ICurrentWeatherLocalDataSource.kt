package com.example.weather_report.model.local

import com.example.weather_report.model.pojo.CurrentWeather

interface ICurrentWeatherLocalDataSource {
    suspend fun insertCurrentWeather(currentWeather: CurrentWeather)
    suspend fun removeCurrentWeather(currentWeather: CurrentWeather)
    suspend fun getAllCurrentWeather(): List<CurrentWeather>
    suspend fun deleteAllCurrentWeather()
    suspend fun getCurrentWeatherByCityID(id : Int) : CurrentWeather?
    suspend fun updateCurrentWeather(currentWeather: CurrentWeather)
    suspend fun deleteCurrentWeatherByCityID(id : Int)
    suspend fun doesCurrentWeatherExistForCity(id : Int) : Boolean
}