package com.example.weather_report.model.repository

import com.example.weather_report.model.local.CityLocalDataSourceImpl
import com.example.weather_report.model.local.CurrentWeatherLocalDataSourceImpl
import com.example.weather_report.model.local.ForecastItemLocalDataSourceImpl
import com.example.weather_report.model.pojo.City
import com.example.weather_report.model.pojo.ForecastItem
import com.example.weather_report.model.remote.WeatherAndForecastRemoteDataSourceImpl
import com.example.weather_report.model.remote.ForecastResponse
import com.example.weather_report.model.remote.WeatherResponse

class WeatherRepositoryImpl private constructor(
    private val local_city : CityLocalDataSourceImpl,
    private val local_forecast : ForecastItemLocalDataSourceImpl,
    private val local_currWeather : CurrentWeatherLocalDataSourceImpl,
    private val remote : WeatherAndForecastRemoteDataSourceImpl
) : IWeatherRepository {
    companion object {
        private var repo : WeatherRepositoryImpl? = null
        fun getInstance(
            _local_city : CityLocalDataSourceImpl,
            _local_forecast : ForecastItemLocalDataSourceImpl,
            _local_currWeather : CurrentWeatherLocalDataSourceImpl,
            _remote : WeatherAndForecastRemoteDataSourceImpl
        ) : WeatherRepositoryImpl {
            return repo ?: synchronized(this) {
                val temp = WeatherRepositoryImpl(_local_city, _local_forecast, _local_currWeather, _remote)
                repo = temp
                temp
            }
        }
    }

    override suspend fun fetchForecastDataRemotely(
        lat : Double,
        lon : Double,
        units : String
    ) : ForecastResponse? {
        return remote.makeNetworkCallToGetForecast(lat, lon, units)
    }

    override suspend fun fetchCurrentWeatherDataRemotely(
        lat : Double,
        lon : Double,
        units : String
    ) : WeatherResponse? {
        return remote.makeNetworkCallToGetCurrentWeather(lat, lon, units)
    }

    override suspend fun fetchFavouriteCitiesLocally() : List<City> {
        return local_city.getAllCities()
    }

    override suspend fun deleteCityFromFavourites(city : City) {
        local_city.removeCity(city)
    }

    override suspend fun addCityToFavourites(city : City) {
        local_city.insertCity(city)
    }

    override suspend fun fetchAllSavedLocationForecastDataLocally() : List<ForecastItem> {
        return local_forecast.getAllForecastItems()
    }

    override suspend fun deleteAllSavedLocationForecastData() {
        local_forecast.deleteAllForecastItems()
    }

    override suspend fun saveLocationForecastData(forecastItems : List<ForecastItem>) {
        local_forecast.insertAllForecastItems(forecastItems)
    }

    override suspend fun saveLocationSingleForecastData(forecastItem: ForecastItem) {
        local_forecast.insertForecastItem(forecastItem)
    }

    override suspend fun deleteLocationSingleForecastData(forecastItem: ForecastItem) {
        local_forecast.removeForecastItem(forecastItem)
    }
}