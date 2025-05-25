//package com.example.weather_report.features.home.viewmodel
//
//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.weather_report.model.pojo.ForecastResponse
//import com.example.weather_report.model.pojo.WeatherResponse
//import com.example.weather_report.model.repository.IWeatherRepository
//import com.example.weather_report.utils.AppliedSystemSettings
//import com.example.weather_report.utils.UnitSystem
//import com.example.weather_report.utils.UnitSystemsConversions
//import com.example.weather_report.utils.Units
//import kotlinx.coroutines.launch
//
//class HomeScreenViewModel(private val repo : IWeatherRepository) : ViewModel() {
//    private val _weatherResponse = MutableLiveData<WeatherResponse?>()
//    val weatherResponse: LiveData<WeatherResponse?> = _weatherResponse
//
//    private val _forecastResponse = MutableLiveData<ForecastResponse?>()
//    val forecastResponse: LiveData<ForecastResponse?> = _forecastResponse
//
//    fun fetchWeatherData(lat: Double, lon: Double) {
//        viewModelScope.launch {
//            try {
//                var response : WeatherResponse?
//
//                if (AppliedSystemSettings.unitSystem == UnitSystem.CUSTOM.value) {
//                    response = repo.fetchCurrentWeatherDataRemotely(lat, lon, UnitSystem.METRIC.value)
//
//                    // temp (actual && high && low && feels like) conversions
//                    if (response != null) {
//                        response.main.temp = when(AppliedSystemSettings.tempUnit) {
//                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(response.main.temp)
//                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(response.main.temp)
//                            else -> response.main.temp
//                        }
//
//                        response.main.temp_kf = when(AppliedSystemSettings.tempUnit) {
//                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(response.main.temp_kf)
//                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(response.main.temp_kf)
//                            else -> response.main.temp_kf
//                        }
//
//                        response.main.feels_like = when(AppliedSystemSettings.tempUnit) {
//                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(response.main.feels_like)
//                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(response.main.feels_like)
//                            else -> response.main.feels_like
//                        }
//
//                        response.main.temp_min = when(AppliedSystemSettings.tempUnit) {
//                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(response.main.temp_min)
//                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(response.main.temp_min)
//                            else -> response.main.temp_min
//                        }
//
//                        response.main.temp_max = when(AppliedSystemSettings.tempUnit) {
//                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(response.main.temp_max)
//                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(response.main.temp_max)
//                            else -> response.main.temp_max
//                        }
//
//
//                        // speed conversions
//                        response.wind.speed = when(AppliedSystemSettings.speedUnit) {
//                            Units.KILOMETERS_PER_HOUR.symbol -> UnitSystemsConversions.meterPerSecondToKilometerPerHour(response.wind.speed)
//                            Units.MILES_PER_HOUR.symbol -> UnitSystemsConversions.meterPerSecondToMilePerHour(response.wind.speed)
//                            Units.FEET_PER_SECOND.symbol -> UnitSystemsConversions.meterPerSecondToFeetPerSecond(response.wind.speed)
//                            else -> response.wind.speed
//                        }
//
//                        // pressure conversions
//                        response.main.pressure = when(AppliedSystemSettings.pressureUnit) {
//                            Units.ATMOSPHERE.symbol -> UnitSystemsConversions.hectopascalToAtm(response.main.pressure.toDouble()).toInt()
//                            Units.BAR.symbol -> UnitSystemsConversions.hectopascalToBar(response.main.pressure.toDouble()).toInt()
//                            Units.PSI.symbol -> UnitSystemsConversions.hectopascalToPsi(response.main.pressure.toDouble()).toInt()
//                            else -> response.main.pressure
//                        }
//                    }
//                }
//                else {
//                    response = repo.fetchCurrentWeatherDataRemotely(lat, lon, AppliedSystemSettings.unitSystem)
//                }
//
//                Log.i("TAG", "fetchWeatherData: ${response}")
//
//                _weatherResponse.postValue(response)
//
//            } catch (e: Exception) {
//                _weatherResponse.postValue(null)
//            }
//        }
//    }
//
//    fun fetchForecastData(lat: Double, lon: Double) {
//        viewModelScope.launch {
//            try {
//                var response: ForecastResponse?
//
//                if (AppliedSystemSettings.unitSystem == UnitSystem.CUSTOM.value) {
//                    // Fetch data in metric units for conversion
//                    response = repo.fetchForecastDataRemotely(lat, lon, UnitSystem.METRIC.value)
//
//                    response?.list?.forEach { forecastItem ->
//
//                        // temp (actual && high && low && feels like) conversions
//                        forecastItem.main.temp = when (AppliedSystemSettings.tempUnit) {
//                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(forecastItem.main.temp)
//                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(forecastItem.main.temp)
//                            else -> forecastItem.main.temp
//                        }
//
//                        forecastItem.main.temp_kf = when (AppliedSystemSettings.tempUnit) {
//                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(forecastItem.main.temp_kf)
//                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(forecastItem.main.temp_kf)
//                            else -> forecastItem.main.temp_kf
//                        }
//
//                        forecastItem.main.feels_like = when (AppliedSystemSettings.tempUnit) {
//                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(forecastItem.main.feels_like)
//                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(forecastItem.main.feels_like)
//                            else -> forecastItem.main.feels_like
//                        }
//
//                        forecastItem.main.temp_min = when (AppliedSystemSettings.tempUnit) {
//                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(forecastItem.main.temp_min)
//                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(forecastItem.main.temp_min)
//                            else -> forecastItem.main.temp_min
//                        }
//
//                        forecastItem.main.temp_max = when (AppliedSystemSettings.tempUnit) {
//                            Units.KELVIN.symbol -> UnitSystemsConversions.celsiusToKelvin(forecastItem.main.temp_max)
//                            Units.FAHRENHEIT.symbol -> UnitSystemsConversions.celsiusToFahrenheit(forecastItem.main.temp_max)
//                            else -> forecastItem.main.temp_max
//                        }
//
//                        // wind speed conversion
//                        forecastItem.wind.speed = when (AppliedSystemSettings.speedUnit) {
//                            Units.KILOMETERS_PER_HOUR.symbol -> UnitSystemsConversions.meterPerSecondToKilometerPerHour(forecastItem.wind.speed)
//                            Units.MILES_PER_HOUR.symbol -> UnitSystemsConversions.meterPerSecondToMilePerHour(forecastItem.wind.speed)
//                            Units.FEET_PER_SECOND.symbol -> UnitSystemsConversions.meterPerSecondToFeetPerSecond(forecastItem.wind.speed)
//                            else -> forecastItem.wind.speed
//                        }
//
//                        // pressure conversion
//                        forecastItem.main.pressure = when (AppliedSystemSettings.pressureUnit) {
//                            Units.ATMOSPHERE.symbol -> UnitSystemsConversions.hectopascalToAtm(forecastItem.main.pressure.toDouble()).toInt()
//                            Units.BAR.symbol -> UnitSystemsConversions.hectopascalToBar(forecastItem.main.pressure.toDouble()).toInt()
//                            Units.PSI.symbol -> UnitSystemsConversions.hectopascalToPsi(forecastItem.main.pressure.toDouble()).toInt()
//                            else -> forecastItem.main.pressure
//                        }
//                    }
//                } else {
//                    response = repo.fetchForecastDataRemotely(lat, lon, AppliedSystemSettings.unitSystem)
//                }
//
//                Log.i("TAG", "fetchForecastData: ${response}")
//                _forecastResponse.postValue(response)
//
//            } catch (e: Exception) {
//                _forecastResponse.postValue(null)
//            }
//        }
//    }
//}
