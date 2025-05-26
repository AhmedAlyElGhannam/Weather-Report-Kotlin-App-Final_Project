//package com.example.weather_report.model.local
//
//import com.example.weather_report.model.pojo.ForecastResponse
//import com.example.weather_report.model.pojo.WeatherResponse
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//class LocalForecastDataSourceImpl(private val dao : ILocalForecastResponseDao) : ILocalForecastDataSource {
//    override suspend fun saveForecastResponse(cityId: Int, response: ForecastResponse) {
//        withContext(Dispatchers.IO) {
//            dao.insert(LocalForecastResponse(cityId, Converters().forecastResponseToJson(response), System.currentTimeMillis()))
//        }
//    }
//
//    override suspend fun getForecastResponse(cityId: Int): ForecastResponse? {
//        return withContext(Dispatchers.IO) {
//            val cached = dao.getByCityId(cityId)
//            cached?.let { Converters().jsonToForecastResponse(it.forecastResponse) }
//        }
//    }
//}