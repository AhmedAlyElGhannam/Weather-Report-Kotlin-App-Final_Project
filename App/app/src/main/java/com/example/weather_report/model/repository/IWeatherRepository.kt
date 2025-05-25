package com.example.weather_report.model.repository

import com.example.weather_report.model.pojo.City
import com.example.weather_report.model.pojo.CurrentWeather
import com.example.weather_report.model.pojo.ForecastItem
import com.example.weather_report.model.pojo.ForecastResponse
import com.example.weather_report.model.pojo.WeatherResponse

interface IWeatherRepository {
    suspend fun fetchForecastDataRemotely(
        lat : Double,
        lon : Double,
        units : String,
        lang: String
    ) : ForecastResponse?

    suspend fun fetchCurrentWeatherDataRemotely(
        lat : Double,
        lon : Double,
        units : String,
        lang: String
    ) : WeatherResponse?

    suspend fun fetchFavouriteCitiesLocally(): List<City>
    suspend fun deleteCityFromFavourites(city: City)
    suspend fun addCityToFavourites(city: City)
    suspend fun getCityByID(id: Int): City?
    suspend fun updateCityInfo(city: City)
    suspend fun isFavouriteCity(id: Int): Boolean

    suspend fun fetchAllSavedLocationForecastDataLocally(): List<ForecastItem>
    suspend fun deleteAllSavedLocationForecastData()
    suspend fun saveLocationForecastData(forecastItems: List<ForecastItem>)
    suspend fun saveLocationSingleForecastData(forecastItem: ForecastItem)
    suspend fun deleteLocationSingleForecastData(forecastItem: ForecastItem)
    suspend fun getForecastItemsByCityID(id: Int): List<ForecastItem>
    suspend fun updateForecastItem(forecastItem: ForecastItem)

    suspend fun insertCurrentWeather(currentWeather: CurrentWeather)
    suspend fun removeCurrentWeather(currentWeather: CurrentWeather)
    suspend fun getAllCurrentWeather(): List<CurrentWeather>
    suspend fun deleteAllCurrentWeather()
    suspend fun getCurrentWeatherByCityID(id: Int): CurrentWeather?
    suspend fun updateCurrentWeather(currentWeather: CurrentWeather)
    suspend fun deleteCurrentWeatherByCityID(id: Int)
    suspend fun doesCurrentWeatherExistForCity(id: Int): Boolean
}