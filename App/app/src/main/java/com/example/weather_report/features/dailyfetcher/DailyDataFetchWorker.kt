package com.example.weather_report.features.dailyfetcher

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weather_report.model.repository.WeatherRepositoryImpl

class DailyDataFetchWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
//        val repo = WeatherRepositoryImpl.getInstance(/* dependencies */)
//        val location = repo.getSelectedLocation()
//        location?.let {
//            repo.fetchCurrentWeatherDataRemotely(it.lat, it.lon, "metric", "en")
//            repo.fetchForecastDataRemotely(it.lat, it.lon, "metric", "en")
//        }
        return Result.success()
    }
}