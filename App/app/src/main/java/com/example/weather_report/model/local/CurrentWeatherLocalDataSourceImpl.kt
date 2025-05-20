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
        withContext(Dispatchers.IO) {
            dao.deleteAllCurrentWeather()
        }
    }

    override suspend fun getCurrentWeatherByCityID(id : Int) : CurrentWeather? {
        return withContext(Dispatchers.IO) {
            dao.getCurrentWeatherByCityId(id)
        }
    }

    override suspend fun updateCurrentWeather(currentWeather: CurrentWeather) {
        withContext(Dispatchers.IO) {
            dao.updateCurrentWeather(currentWeather)
        }
    }

    override suspend fun deleteCurrentWeatherByCityID(id : Int) {
        withContext(Dispatchers.IO) {
            dao.deleteCurrentWeatherByCityId(id)
        }
    }

    override suspend fun doesCurrentWeatherExistForCity(id : Int) : Boolean {
        return withContext(Dispatchers.IO) {
            dao.doesCurrentWeatherExistForCity(id)
        }
    }
}
