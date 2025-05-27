package com.example.weather_report.features.dailyfetcher

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weather_report.model.local.LocalDB
import com.example.weather_report.model.local.LocalDataSourceImpl
import com.example.weather_report.model.remote.IWeatherService
import com.example.weather_report.model.remote.RetrofitHelper
import com.example.weather_report.model.remote.WeatherAndForecastRemoteDataSourceImpl
import com.example.weather_report.model.repository.WeatherRepositoryImpl

class DailyDataFetchWorker(val context: Context, val params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val repo = WeatherRepositoryImpl.getInstance(
            WeatherAndForecastRemoteDataSourceImpl(
            RetrofitHelper.retrofit.create(IWeatherService::class.java))
            , LocalDataSourceImpl(LocalDB.getInstance(context).weatherDao())
        )
        val location = repo.getCurrentLocationWithWeather(true, isNetworkAvailable = true)
        location?.let {
            repo.fetchCurrentWeatherDataRemotely(it.location.latitude, it.location.longitude, "metric", "en")
            repo.fetchForecastDataRemotely(it.location.latitude, it.location.longitude, "metric", "en")
        }
        return Result.success()
    }
}