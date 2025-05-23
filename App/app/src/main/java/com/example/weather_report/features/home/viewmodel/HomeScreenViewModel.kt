package com.example.weather_report.features.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_report.model.pojo.ForecastResponse
import com.example.weather_report.model.pojo.WeatherResponse
import com.example.weather_report.model.repository.IWeatherRepository
import com.example.weather_report.utils.ChosenDataUnits
import com.example.weather_report.utils.UnitSystem
import com.example.weather_report.utils.UnitSystemsConversions
import com.example.weather_report.utils.Units
import kotlinx.coroutines.launch

class HomeScreenViewModel(private val repo : IWeatherRepository) : ViewModel() {
    private val _weatherResponse = MutableLiveData<WeatherResponse?>()
    val weatherResponse: LiveData<WeatherResponse?> = _weatherResponse

    private val _forecastResponse = MutableLiveData<ForecastResponse?>()
    val forecastResponse: LiveData<ForecastResponse?> = _forecastResponse

    fun fetchWeatherData(lat: Double, lon: Double, units: String) {
        viewModelScope.launch {
            try {
                var response : WeatherResponse?

                if (ChosenDataUnits.unitSystem == UnitSystem.CUSTOM.value) {
                    response = repo.fetchCurrentWeatherDataRemotely(lat, lon, UnitSystem.METRIC.value)

                    // convert all units in response depending on what is chosen

                    // temp (actual && high && low && feels like)
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

                _weatherResponse.postValue(response)
            } catch (e: Exception) {
                _weatherResponse.postValue(null)
            }
        }
    }

    fun fetchForecastData(lat: Double, lon: Double, units: String) {
        viewModelScope.launch {
            try {
                val response = repo.fetchForecastDataRemotely(lat, lon, units)
                _forecastResponse.postValue(response)
            } catch (e: Exception) {
                _forecastResponse.postValue(null)
            }
        }
    }
}
