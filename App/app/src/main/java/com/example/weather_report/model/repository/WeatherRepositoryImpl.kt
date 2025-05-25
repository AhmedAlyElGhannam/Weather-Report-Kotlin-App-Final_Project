package com.example.weather_report.model.repository

import com.example.weather_report.model.local.CityLocalDataSourceImpl
import com.example.weather_report.model.local.CurrentWeatherLocalDataSourceImpl
import com.example.weather_report.model.local.ForecastItemLocalDataSourceImpl
import com.example.weather_report.model.local.ICityLocalDataSource
import com.example.weather_report.model.local.ICurrentWeatherLocalDataSource
import com.example.weather_report.model.local.IForecastItemLocalDataSource
import com.example.weather_report.model.pojo.City
import com.example.weather_report.model.pojo.CurrentWeather
import com.example.weather_report.model.pojo.ForecastItem
import com.example.weather_report.model.remote.WeatherAndForecastRemoteDataSourceImpl
import com.example.weather_report.model.pojo.ForecastResponse
import com.example.weather_report.model.pojo.WeatherResponse
import com.example.weather_report.model.remote.IWeatherAndForecastRemoteDataSource

class WeatherRepositoryImpl private constructor(
    private val local_city : ICityLocalDataSource,
    private val local_forecast : IForecastItemLocalDataSource,
    private val local_currWeather : ICurrentWeatherLocalDataSource,
    private val remote : IWeatherAndForecastRemoteDataSource
) : IWeatherRepository {
    companion object {
        private var repo : WeatherRepositoryImpl? = null
        fun getInstance(
            _local_city : ICityLocalDataSource,
            _local_forecast : IForecastItemLocalDataSource,
            _local_currWeather : ICurrentWeatherLocalDataSource,
            _remote : IWeatherAndForecastRemoteDataSource
        ) : WeatherRepositoryImpl {
            return repo ?: synchronized(this) {
                val temp = WeatherRepositoryImpl(_local_city, _local_forecast, _local_currWeather, _remote)
                repo = temp
                temp
            }
        }
    }

    override suspend fun fetchForecastDataRemotely(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ) : ForecastResponse? {
        return remote.makeNetworkCallToGetForecast(lat, lon, units, lang)
    }

    override suspend fun fetchCurrentWeatherDataRemotely(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ) : WeatherResponse? {
        return remote.makeNetworkCallToGetCurrentWeather(lat, lon, units, lang)
    }

    override suspend fun fetchFavouriteCitiesLocally(): List<City> {
        return local_city.getAllCities()
    }

    override suspend fun deleteCityFromFavourites(city: City) {
        local_city.removeCity(city)
    }

    override suspend fun addCityToFavourites(city: City) {
        local_city.insertCity(city)
    }

    override suspend fun getCityByID(id: Int): City? {
        return local_city.getCityByID(id)
    }

    override suspend fun updateCityInfo(city: City) {
        local_city.updateCityInfo(city)
    }

    override suspend fun isFavouriteCity(id: Int): Boolean {
        return local_city.isFavouriteCity(id)
    }

    override suspend fun fetchAllSavedLocationForecastDataLocally(): List<ForecastItem> {
        return local_forecast.getAllForecastItems()
    }

    override suspend fun deleteAllSavedLocationForecastData() {
        local_forecast.deleteAllForecastItems()
    }

    override suspend fun saveLocationForecastData(forecastItems: List<ForecastItem>) {
        local_forecast.insertAllForecastItems(forecastItems)
    }

    override suspend fun saveLocationSingleForecastData(forecastItem: ForecastItem) {
        local_forecast.insertForecastItem(forecastItem)
    }

    override suspend fun deleteLocationSingleForecastData(forecastItem: ForecastItem) {
        local_forecast.removeForecastItem(forecastItem)
    }

    override suspend fun getForecastItemsByCityID(id: Int): List<ForecastItem> {
        return local_forecast.getForcastItemsByCityID(id)
    }

    override suspend fun updateForecastItem(forecastItem: ForecastItem) {
        local_forecast.updateForecastItem(forecastItem)
    }

    override suspend fun insertCurrentWeather(currentWeather: CurrentWeather) {
        local_currWeather.insertCurrentWeather(currentWeather)
    }

    override suspend fun removeCurrentWeather(currentWeather: CurrentWeather) {
        local_currWeather.removeCurrentWeather(currentWeather)
    }

    override suspend fun getAllCurrentWeather(): List<CurrentWeather> {
        return local_currWeather.getAllCurrentWeather()
    }

    override suspend fun deleteAllCurrentWeather() {
        local_currWeather.deleteAllCurrentWeather()
    }

    override suspend fun getCurrentWeatherByCityID(id: Int): CurrentWeather? {
        return local_currWeather.getCurrentWeatherByCityID(id)
    }

    override suspend fun updateCurrentWeather(currentWeather: CurrentWeather) {
        local_currWeather.updateCurrentWeather(currentWeather)
    }

    override suspend fun deleteCurrentWeatherByCityID(id: Int) {
        local_currWeather.deleteCurrentWeatherByCityID(id)
    }

    override suspend fun doesCurrentWeatherExistForCity(id: Int): Boolean {
        return local_currWeather.doesCurrentWeatherExistForCity(id)
    }
}