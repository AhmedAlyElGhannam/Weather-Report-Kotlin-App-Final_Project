package com.example.weather_report.model.local

import com.example.weather_report.model.pojo.ForecastItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ForecastItemLocalDataSourceImpl(private val dao : IForecastItemDao) : IForecastItemLocalDataSource {
    override suspend fun insertForecastItem(forecastItem: ForecastItem) {
        withContext(Dispatchers.IO) {
            dao.insertSingleForecastItem(forecastItem)
        }
    }

    override suspend fun removeForecastItem(forecastItem: ForecastItem) {
        withContext(Dispatchers.IO) {
            dao.deleteSingleForecastItem(forecastItem)
        }
    }

    override suspend fun getAllForecastItems(): List<ForecastItem> {
        return withContext(Dispatchers.IO) {
            dao.getAllForecastItems()
        }
    }

    override suspend fun insertAllForecastItems(forecastItems : List<ForecastItem>) {
        withContext(Dispatchers.IO){
            dao.insertAllForecastItems(forecastItems)
        }
    }

    override suspend fun deleteAllForecastItems() {
        withContext(Dispatchers.IO){
            dao.deleteAllForecastItems()
        }
    }

    override suspend fun getForcastItemsByCityID(id : Int) : List<ForecastItem> {
        return withContext(Dispatchers.IO) {
            dao.getForecastItemsByCityId(id)
        }
    }

    override suspend fun updateForecastItem(forecastItem: ForecastItem) {
        withContext(Dispatchers.IO) {
            dao.updateForecastItem(forecastItem)
        }
    }
}