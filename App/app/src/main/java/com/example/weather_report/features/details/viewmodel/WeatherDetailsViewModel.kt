package com.example.weather_report.features.details.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_report.contracts.WeatherDetailsContract
import com.example.weather_report.model.pojo.sub.ForecastItem
import com.example.weather_report.model.pojo.response.ForecastResponse
import com.example.weather_report.model.pojo.entity.LocationWithWeather
import com.example.weather_report.model.pojo.response.WeatherResponse
import com.example.weather_report.model.repository.IWeatherRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.abs

class WeatherDetailsViewModel(private val repo: IWeatherRepository)
    : ViewModel(), WeatherDetailsContract.ViewModel {
    private val _selectedFavoriteLocation = MutableLiveData<LocationWithWeather?>()

    private val _hourlyWeatherItemsList = MutableLiveData<List<ForecastItem>?>()
    val hourlyWeatherItemsList: LiveData<List<ForecastItem>?> = _hourlyWeatherItemsList

    private val _dailyWeatherItemsList = MutableLiveData<List<ForecastItem>>()
    val dailyWeatherItemsList: LiveData<List<ForecastItem>> = _dailyWeatherItemsList

    private val _weatherResponse = MutableLiveData<WeatherResponse?>()
    val weatherResponse: LiveData<WeatherResponse?> = _weatherResponse

    override fun refreshLocationData() {
        viewModelScope.launch {
            _selectedFavoriteLocation.value?.location?.let { repo.refreshLocation(it.id) }
            _selectedFavoriteLocation.value?.location?.let {
                repo.getLocationWithWeather(it.id)?.let {
                    setFavoriteLocationData(
                        it
                    )
                }
            }
        }
    }

    override fun setFavoriteLocationData(locationWithWeather: LocationWithWeather) {
        viewModelScope.launch {
            _selectedFavoriteLocation.value = locationWithWeather
            _hourlyWeatherItemsList.postValue(locationWithWeather.forecast?.let {
                filterForecastForToday(
                    it
                )
            })
            _dailyWeatherItemsList.postValue(locationWithWeather.forecast?.let {
                filterDailyForecast(
                    it
                )
            })
            _weatherResponse.postValue(locationWithWeather.currentWeather)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun filterForecastForToday(forecastResponse: ForecastResponse): List<ForecastItem> {
        val totalSeconds = forecastResponse.city.timezone
        val hours = totalSeconds / 3600
        val remainingSeconds = totalSeconds % 3600
        val minutes = remainingSeconds / 60
        val sign = if (totalSeconds >= 0) "+" else "-"
        val tzId = String.format("GMT%s%02d:%02d", sign, abs(hours), abs(minutes))
        val timeZone = TimeZone.getTimeZone(tzId)

        val calendar = Calendar.getInstance(timeZone).apply {
            timeInMillis = System.currentTimeMillis()
        }
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        return forecastResponse.list.filter { item ->
            val dtTxt = item.dt_txt
            val datePart = dtTxt.substring(0, 10)
            val parts = datePart.split("-")
            if (parts.size != 3) return@filter false

            val year = parts[0].toIntOrNull() ?: return@filter false
            val month = parts[1].toIntOrNull() ?: return@filter false
            val day = parts[2].toIntOrNull() ?: return@filter false

            year == currentYear && month == currentMonth && day == currentDay
        }.also {
            Log.i("TAG", "Filtered hourly items count: ${it.size}")
        }
    }

    @SuppressLint("DefaultLocale")
    private fun filterDailyForecast(forecastResponse: ForecastResponse): List<ForecastItem> {
        val totalSeconds = forecastResponse.city.timezone
        val hours = totalSeconds / 3600
        val remainingSeconds = totalSeconds % 3600
        val minutes = remainingSeconds / 60
        val sign = if (totalSeconds >= 0) "+" else "-"
        val tzId = String.format("GMT%s%02d:%02d", sign, abs(hours), abs(minutes))
        val timeZone = TimeZone.getTimeZone(tzId)

        val calendar = Calendar.getInstance(timeZone).apply {
            timeInMillis = System.currentTimeMillis()
        }
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val dateMap = mutableMapOf<String, MutableList<ForecastItem>>()
        forecastResponse.list.forEach { item ->
            calendar.timeInMillis = item.dt * 1000L
            val itemYear = calendar.get(Calendar.YEAR)
            val itemMonth = calendar.get(Calendar.MONTH) + 1
            val itemDay = calendar.get(Calendar.DAY_OF_MONTH)

            if (itemYear == currentYear && itemMonth == currentMonth && itemDay == currentDay) {
                return@forEach
            }

            val dateKey = "$itemYear-$itemMonth-$itemDay"
            if (!dateMap.containsKey(dateKey)) {
                dateMap[dateKey] = mutableListOf()
            }
            dateMap[dateKey]?.add(item)
        }

        val dailyList = mutableListOf<ForecastItem>()
        dateMap.forEach { (_, items) ->
            var closestToMidday: ForecastItem? = null
            var minTimeDiff = Long.MAX_VALUE

            items.forEach { item ->
                calendar.timeInMillis = item.dt * 1000L
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                val diff = abs((hour - 12) * 60 + minute).toLong()
                if (diff < minTimeDiff) {
                    minTimeDiff = diff
                    closestToMidday = item
                }
            }
            closestToMidday?.let { dailyList.add(it) }
        }

        return dailyList.sortedBy { it.dt }.also {
            Log.i("TAG", "Filtered daily items count: ${it.size}")
        }
    }
}