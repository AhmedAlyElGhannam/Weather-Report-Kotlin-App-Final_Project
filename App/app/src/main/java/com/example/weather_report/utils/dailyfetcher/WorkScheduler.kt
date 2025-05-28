package com.example.weather_report.utils

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weather_report.features.dailyfetcher.DailyDataFetchWorker
import java.util.concurrent.TimeUnit

object WorkScheduler {
    private const val WORKER_TAG = "daily_data_fetch_worker"
    private const val REPEAT_INTERVAL = 14L

    fun scheduleDailyDataFetch(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<DailyDataFetchWorker>(
            REPEAT_INTERVAL,
            TimeUnit.HOURS
        )
            .addTag(WORKER_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORKER_TAG,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelDailyDataFetch(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(WORKER_TAG)
    }
}