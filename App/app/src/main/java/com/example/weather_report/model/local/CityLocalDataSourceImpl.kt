package com.example.weather_report.model.local

import com.example.weather_report.model.local.ICityDao
import com.example.weather_report.model.pojo.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CityLocalDataSourceImpl(private val dao : ICityDao) : ICityLocalDataSource {
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

    override suspend fun getCityByID(id : Int) : City? {
        return withContext(Dispatchers.IO) {
            dao.getCityById(id)
        }
    }

    override suspend fun updateCityInfo(city : City) {
        withContext(Dispatchers.IO) {
            dao.updateCity(city)
        }
    }

    override suspend fun isFavouriteCity(id : Int) : Boolean {
        return withContext(Dispatchers.IO) {
            dao.doesCityExist(id)
        }
    }
}