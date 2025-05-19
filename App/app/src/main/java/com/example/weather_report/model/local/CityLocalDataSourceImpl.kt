package com.example.mvvm.model.local

import com.example.weather_report.model.local.ICityDao
import com.example.weather_report.model.pojo.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CityLocalDataSourceImpl(val dao : ICityDao) : ICityLocalDataSource {
    override suspend fun insertCity(city: City) {
        withContext(Dispatchers.IO) {
            dao.insertSingleCity(city)
        }
    }

    override suspend fun removeCity(city: City) {
        withContext(Dispatchers.IO) {
            dao.deleteSingleCity(city)
        }
    }

    override suspend fun getAllCities(): List<City> {
        return withContext(Dispatchers.IO) {
            dao.getAllCities()
        }
    }
}