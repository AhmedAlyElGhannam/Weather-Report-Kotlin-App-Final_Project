package com.example.weather_report.model.local

import com.example.weather_report.model.pojo.CurrentWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CurrentWeatherLocalDataSourceImpl(private val dao: ICurrentWeatherDao) : ICurrentWeatherLocalDataSource {
    override suspend fun insertCurrentWeather(currentWeather : CurrentWeather) {
        withContext(Dispatchers.IO) {
            dao.insertCurrentWeather(currentWeather)
        }
    }

    override suspend fun removeCurrentWeather(currentWeather : CurrentWeather) {
        withContext(Dispatchers.IO) {
            dao.deleteCurrentWeather(currentWeather)
        }
    }

    override suspend fun getAllCurrentWeather() : List<CurrentWeather> {
        return withContext(Dispatchers.IO) {
            dao.getAllCurrentWeather()
        }
    }

    override suspend fun deleteAllCurrentWeather() {
        dao.deleteAllCurrentWeather()
    }
}
