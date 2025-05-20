package com.example.weather_report

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.weather_report.databinding.HomeScreenBinding
import com.example.weather_report.model.local.CityLocalDataSourceImpl
import com.example.weather_report.model.local.CurrentWeatherLocalDataSourceImpl
import com.example.weather_report.model.local.ForecastItemLocalDataSourceImpl
import com.example.weather_report.model.local.LocalDB
import com.example.weather_report.model.remote.IWeatherService
import com.example.weather_report.model.remote.RetrofitHelper
import com.example.weather_report.model.remote.WeatherAndForecastRemoteDataSourceImpl
import com.example.weather_report.model.repository.WeatherRepositoryImpl
import com.example.weather_report.utils.UnitSystem
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    lateinit var binder : HomeScreenBinding

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen)

        val repo : WeatherRepositoryImpl = WeatherRepositoryImpl.getInstance(
            CityLocalDataSourceImpl(LocalDB.getInstance(this@MainActivity).getCityDao()),
            ForecastItemLocalDataSourceImpl(LocalDB.getInstance(this@MainActivity).getForecastItemDao()),
            CurrentWeatherLocalDataSourceImpl(LocalDB.getInstance(this@MainActivity).getCurrentWeatherDao()),
            WeatherAndForecastRemoteDataSourceImpl(RetrofitHelper.retrofit.create(IWeatherService::class.java))
        )

        GlobalScope.launch(Dispatchers.IO) {
            val res_forecast = repo.fetchForecastDataRemotely(
                lat = 39.0444,
                lon = 69.2357,
                units = UnitSystem.METRIC.value
            )

            val res_currWeather = repo.fetchCurrentWeatherDataRemotely(
                lat = 39.0444,
                lon = 69.2357,
                units = UnitSystem.METRIC.value
            )

            res_forecast?.city?.let { repo.addCityToFavourites(it) }

            withContext(Dispatchers.Main) {
                Log.i("TAG", "forecast: " + res_forecast.toString())
                Log.i("TAG", "currWeather: " + res_currWeather.toString())
            }
        }
    }
}