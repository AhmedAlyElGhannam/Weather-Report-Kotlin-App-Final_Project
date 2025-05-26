//package com.example.weather_report.model.local
//
//import com.example.weather_report.model.pojo.ForecastResponse
//import com.example.weather_report.model.pojo.WeatherResponse
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//class LocalWeatherDataSourceImpl(private val dao : ILocalWeatherResponseDao) : ILocalWeatherDataSource {
//    override suspend fun saveWeatherResponse(cityId: Int, response: WeatherResponse) {
//        withContext(Dispatchers.IO) {
//            dao.insert(LocalWeatherResponse(cityId, Converters().weatherResponseToJson(response), System.currentTimeMillis()))
//        }
//    }
//
//    override suspend fun getWeatherResponse(cityId: Int): WeatherResponse? {
//        return withContext(Dispatchers.IO) {
//            val cached = dao.getByCityId(cityId)
//            cached?.let { Converters().jsonToWeatherResponse(it.weatherResponse) }
//        }
//    }
//}