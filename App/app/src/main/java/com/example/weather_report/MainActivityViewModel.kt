package com.example.weather_report

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_report.model.pojo.Coordinates
import com.example.weather_report.model.pojo.ForecastItem
import com.example.weather_report.model.pojo.ForecastResponse
import com.example.weather_report.model.pojo.WeatherResponse
import com.example.weather_report.model.repository.IWeatherRepository
import com.example.weather_report.utils.ChosenDataUnits
import com.example.weather_report.utils.UnitSystem
import com.example.weather_report.utils.UnitSystemsConversions
import com.example.weather_report.utils.Units
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.abs

class MainActivityViewModel(private val repo : IWeatherRepository) : ViewModel() {
    private val _coordinates = MutableLiveData<Coordinates>()
    val coordinates : LiveData<Coordinates> = _coordinates

    private val _weatherResponse = MutableLiveData<WeatherResponse?>()
    val weatherResponse: LiveData<WeatherResponse?> = _weatherResponse

    private val _forecastResponse = MutableLiveData<ForecastResponse?>()
    val forecastResponse: LiveData<ForecastResponse?> = _forecastResponse

    private val _hourlyWeatherItemsList = MutableLiveData<List<ForecastItem?>?>()
    val hourlyWeatherItemsList : LiveData<List<ForecastItem?>?> = _hourlyWeatherItemsList

    fun fetchWeatherData(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                var response : WeatherResponse?

                if (ChosenDataUnits.unitSystem == UnitSystem.CUSTOM.value) {
                    response = repo.fetchCurrentWeatherDataRemotely(lat, lon, UnitSystem.METRIC.value)

                    // temp (actual && high && low && feels like) conversions
                    if (response != null) {
                        response.main.temp = when(ChosenDataUnits.tempUnit) {
                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(response.main.temp)
                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(response.main.temp)
                            else -> response.main.temp
                        }

                        response.main.temp_kf = when(ChosenDataUnits.tempUnit) {
                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(response.main.temp_kf)
                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(response.main.temp_kf)
                            else -> response.main.temp_kf
                        }

                        response.main.feels_like = when(ChosenDataUnits.tempUnit) {
                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(response.main.feels_like)
                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(response.main.feels_like)
                            else -> response.main.feels_like
                        }

                        response.main.temp_min = when(ChosenDataUnits.tempUnit) {
                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(response.main.temp_min)
                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(response.main.temp_min)
                            else -> response.main.temp_min
                        }

                        response.main.temp_max = when(ChosenDataUnits.tempUnit) {
                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(response.main.temp_max)
                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(response.main.temp_max)
                            else -> response.main.temp_max
                        }


                        // speed conversions
                        response.wind.speed = when(ChosenDataUnits.speedUnit) {
                            Units.KILOMETERS_PER_HOUR.symbol -> UnitSystemsConversions.meterPerSecondToKilometerPerHour(response.wind.speed)
                            Units.MILES_PER_HOUR.symbol -> UnitSystemsConversions.meterPerSecondToMilePerHour(response.wind.speed)
                            Units.FEET_PER_SECOND.symbol -> UnitSystemsConversions.meterPerSecondToFeetPerSecond(response.wind.speed)
                            else -> response.wind.speed
                        }

                        // pressure conversions
                        response.main.pressure = when(ChosenDataUnits.pressureUnit) {
                            Units.ATMOSPHERE.symbol -> UnitSystemsConversions.hectopascalToAtm(response.main.pressure.toDouble()).toInt()
                            Units.BAR.symbol -> UnitSystemsConversions.hectopascalToBar(response.main.pressure.toDouble()).toInt()
                            Units.PSI.symbol -> UnitSystemsConversions.hectopascalToPsi(response.main.pressure.toDouble()).toInt()
                            else -> response.main.pressure
                        }
                    }
                }
                else {
                    response = repo.fetchCurrentWeatherDataRemotely(lat, lon, ChosenDataUnits.unitSystem)
                }

                Log.i("TAG", "fetchWeatherData: ${response}")

                _weatherResponse.postValue(response)

            } catch (e: Exception) {
                _weatherResponse.postValue(null)
            }
        }
    }

    fun fetchForecastData(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                var response: ForecastResponse?

                if (ChosenDataUnits.unitSystem == UnitSystem.CUSTOM.value) {
                    // Fetch data in metric units for conversion
                    response = repo.fetchForecastDataRemotely(lat, lon, UnitSystem.METRIC.value)

                    response?.list?.forEach { forecastItem ->

                        // temp (actual && high && low && feels like) conversions
                        forecastItem.main.temp = when (ChosenDataUnits.tempUnit) {
                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(forecastItem.main.temp)
                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(forecastItem.main.temp)
                            else -> forecastItem.main.temp
                        }

                        forecastItem.main.temp_kf = when (ChosenDataUnits.tempUnit) {
                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(forecastItem.main.temp_kf)
                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(forecastItem.main.temp_kf)
                            else -> forecastItem.main.temp_kf
                        }

                        forecastItem.main.feels_like = when (ChosenDataUnits.tempUnit) {
                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(forecastItem.main.feels_like)
                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(forecastItem.main.feels_like)
                            else -> forecastItem.main.feels_like
                        }

                        forecastItem.main.temp_min = when (ChosenDataUnits.tempUnit) {
                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(forecastItem.main.temp_min)
                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(forecastItem.main.temp_min)
                            else -> forecastItem.main.temp_min
                        }

                        forecastItem.main.temp_max = when (ChosenDataUnits.tempUnit) {
                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(forecastItem.main.temp_max)
                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(forecastItem.main.temp_max)
                            else -> forecastItem.main.temp_max
                        }

                        // wind speed conversion
                        forecastItem.wind.speed = when (ChosenDataUnits.speedUnit) {
                            Units.KILOMETERS_PER_HOUR.symbol -> UnitSystemsConversions.meterPerSecondToKilometerPerHour(forecastItem.wind.speed)
                            Units.MILES_PER_HOUR.symbol -> UnitSystemsConversions.meterPerSecondToMilePerHour(forecastItem.wind.speed)
                            Units.FEET_PER_SECOND.symbol -> UnitSystemsConversions.meterPerSecondToFeetPerSecond(forecastItem.wind.speed)
                            else -> forecastItem.wind.speed
                        }

                        // pressure conversion
                        forecastItem.main.pressure = when (ChosenDataUnits.pressureUnit) {
                            Units.ATMOSPHERE.symbol -> UnitSystemsConversions.hectopascalToAtm(forecastItem.main.pressure.toDouble()).toInt()
                            Units.BAR.symbol -> UnitSystemsConversions.hectopascalToBar(forecastItem.main.pressure.toDouble()).toInt()
                            Units.PSI.symbol -> UnitSystemsConversions.hectopascalToPsi(forecastItem.main.pressure.toDouble()).toInt()
                            else -> forecastItem.main.pressure
                        }
                    }
                } else {
                    response = repo.fetchForecastDataRemotely(lat, lon, ChosenDataUnits.unitSystem)
                }

                Log.i("TAG", "fetchForecastData: ${response}")
                _forecastResponse.postValue(response)
                response?.let {
                    filterForecastForToday(it)
                    Log.i("TAG", "fetchForecastData: ${hourlyWeatherItemsList.value?.size}")
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


    fun setCoordinates(_coord : Coordinates) {
        _coordinates.value = _coord
    }
}