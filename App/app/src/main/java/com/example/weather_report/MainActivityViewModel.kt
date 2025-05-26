package com.example.weather_report

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_report.model.pojo.City
import com.example.weather_report.model.pojo.CurrentWeather
import com.example.weather_report.model.pojo.ForecastItem
import com.example.weather_report.model.pojo.ForecastResponse
import com.example.weather_report.model.pojo.WeatherResponse
import com.example.weather_report.model.repository.IWeatherRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.abs

class MainActivityViewModel(private val repo : IWeatherRepository) : ViewModel() {

    private val _weatherResponse = MutableLiveData<WeatherResponse?>()
    val weatherResponse: LiveData<WeatherResponse?> = _weatherResponse

    private val _forecastResponse = MutableLiveData<ForecastResponse?>()
    val forecastResponse: LiveData<ForecastResponse?> = _forecastResponse

    private val _hourlyWeatherItemsList = MutableLiveData<List<ForecastItem?>?>()
    val hourlyWeatherItemsList : LiveData<List<ForecastItem?>?> = _hourlyWeatherItemsList

    private val _dailyWeatherItemsList = MutableLiveData<List<ForecastItem>>()
    val dailyWeatherItemsList: LiveData<List<ForecastItem>> = _dailyWeatherItemsList

    fun fetchWeatherData(isConnected: Boolean, lat: Double, lon: Double, unit: String, lang: String) {
        viewModelScope.launch {
            try {
                if (isConnected) {
                    val response = repo.fetchCurrentWeatherDataRemotely(lat, lon, unit, lang)
                    Log.i("TAG", "fetchWeatherData: ${response}")
                    _weatherResponse.postValue(response)
                    if (response != null) {
//                        repo.setCurrentLocation(City(
//                            id = 1,
//                            coord = response.coord,
//                            country = "",
//                            isCurrLocation = true,
//                            lastUpdated = System.currentTimeMillis(),
//                            name = response.weather[0].main,
//                            population = 0,
//                            timezone = 0,
//                            sunrise = 0,
//                            sunset = 0
//                        ))
//                        repo.insertCurrentLocationWeatherData(response)
//                        Log.i("TAG", "fetchWeatherData: ${repo.getCurrentLocationWeatherLocally()}")
                    }
                }
                else {
//                    val city = repo.getCurrentLocation()

                }

            } catch (e: Exception) {
                _weatherResponse.postValue(null)
            }
        }
    }

    fun fetchForecastData(isConnected: Boolean, lat: Double, lon: Double, unit: String, lang: String) {
        viewModelScope.launch {
            try {
                val response = repo.fetchForecastDataRemotely(lat, lon, unit, lang)
                Log.i("TAG", "fetchForecastData: ${response}")
                _forecastResponse.postValue(response)
                if (response != null) {
//                    repo.insertCurrentLocationForecastData(response)
                }
                response?.let {
                    filterForecastForToday(it)
                    _dailyWeatherItemsList.postValue(filterDailyForecast(it))
                    Log.i("TAG", "fetchForecastData: ${hourlyWeatherItemsList.value?.size}")
//                    Log.i("TAG", "fetchForecastData: ${repo.getCurrentLocationForecastLocally()}")
                }
            } catch (e: Exception) {
                _forecastResponse.postValue(null)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun filterForecastForToday(__forecastResponse: ForecastResponse) {
            val totalSeconds : Int = __forecastResponse.city.timezone
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

            Log.i("TAG", "City timezone: $tzId, Current local date: $currentYear-$currentMonth-$currentDay")

            _hourlyWeatherItemsList.postValue(__forecastResponse.list.filter { item ->
                val dtTxt = item.dt_txt
                val datePart = dtTxt.substring(0, 10)
                val parts = datePart.split("-")
                if (parts.size != 3) return@filter false

                val year = parts[0].toIntOrNull() ?: return@filter false
                val month = parts[1].toIntOrNull() ?: return@filter false
                val day = parts[2].toIntOrNull() ?: return@filter false

                year == currentYear && month == currentMonth && day == currentDay
            })
            Log.i("TAG", "Filtered items count: ${_hourlyWeatherItemsList.value?.size}")
    }

    private fun filterDailyForecast(forecastResponse: ForecastResponse): List<ForecastItem> {
        val totalSeconds = forecastResponse.city.timezone
        // Calculate timezone with hours AND minutes (same as hourly filter)
        val hours = totalSeconds / 3600
        val remainingSeconds = totalSeconds % 3600
        val minutes = remainingSeconds / 60
        val sign = if (totalSeconds >= 0) "+" else "-"
        val tzId = String.format("GMT%s%02d:%02d", sign, abs(hours), abs(minutes))
        val timeZone = TimeZone.getTimeZone(tzId)

        val calendar = Calendar.getInstance(timeZone).apply {
            timeInMillis = System.currentTimeMillis() // Current time in city's timezone
        }
        // Get today's date components
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Group items by date (excluding today)
        val dateMap = mutableMapOf<String, MutableList<ForecastItem>>()

        forecastResponse.list.forEach { item ->
            // Convert item's timestamp to local time
            calendar.timeInMillis = item.dt * 1000L
            val itemYear = calendar.get(Calendar.YEAR)
            val itemMonth = calendar.get(Calendar.MONTH) + 1
            val itemDay = calendar.get(Calendar.DAY_OF_MONTH)

            // Skip today's items
            if (itemYear == currentYear && itemMonth == currentMonth && itemDay == currentDay) {
                return@forEach
            }

            // Group items by date
            val dateKey = "$itemYear-$itemMonth-$itemDay"
            if (!dateMap.containsKey(dateKey)) {
                dateMap[dateKey] = mutableListOf()
            }
            dateMap[dateKey]?.add(item)
        }

        // Select the item closest to midday (12:00 PM) for each day
        val dailyList = mutableListOf<ForecastItem>()
        dateMap.forEach { (_, items) ->
            var closestToMidday: ForecastItem? = null
            var minTimeDiff = Long.MAX_VALUE

            items.forEach { item ->
                calendar.timeInMillis = item.dt * 1000L
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                // Calculate time difference from 12:00 PM (in minutes)
                val diff = abs((hour - 12) * 60 + minute)
                if (diff < minTimeDiff) {
                    minTimeDiff = diff.toLong()
                    closestToMidday = item
                }
            }
            closestToMidday?.let { dailyList.add(it) }
        }

        // Sort by date
        return dailyList.sortedBy { it.dt }
    }

}