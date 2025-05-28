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
import com.example.weather_report.utils.AppliedSystemSettings
import com.example.weather_report.utils.GPSUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DailyDataFetchWorker(val context: Context, val params: WorkerParameters) : CoroutineWorker(context, params) {
    private val appliedSettings by lazy { AppliedSystemSettings.getInstance(context) }
    private val gpsUtils by lazy { GPSUtil(context) }

    override suspend fun doWork(): Result {
        return try {
            val repo = WeatherRepositoryImpl.getInstance(
                WeatherAndForecastRemoteDataSourceImpl(
                    RetrofitHelper.retrofit.create(IWeatherService::class.java)),
                LocalDataSourceImpl(LocalDB.getInstance(context).weatherDao())
            )

            when (appliedSettings.getSelectedLocationOption().optName) {
                "GPS" -> fetchWithGpsLocation(repo)
                else -> fetchWithManualLocation(repo)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun fetchWithGpsLocation(repo: WeatherRepositoryImpl): Boolean {
        return try {
            val location = withContext(Dispatchers.IO) {
                gpsUtils.getCurrentLocationSync()
            }

            location?.let { (lat, lon) ->
                repo.fetchCurrentWeatherDataRemotely(lat, lon, "metric", "en")
                repo.fetchForecastDataRemotely(lat, lon, "metric", "en")
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun fetchWithManualLocation(repo: WeatherRepositoryImpl): Boolean {
        return try {
            val location = repo.getCurrentLocationWithWeather(false, false)
            location?.let {
                repo.fetchCurrentWeatherDataRemotely(
                    it.location.latitude,
                    it.location.longitude,
                    "metric",
                    "en"
                )
                repo.fetchForecastDataRemotely(
                    it.location.latitude,
                    it.location.longitude,
                    "metric",
                    "en"
                )
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }
}