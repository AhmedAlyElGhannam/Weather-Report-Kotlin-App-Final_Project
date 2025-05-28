package com.example.weather_report.model.pojo.entity

import com.example.weather_report.model.pojo.response.ForecastResponse
import com.example.weather_report.model.pojo.response.WeatherResponse

data class LocationWithWeather(val location: LocationEntity, val currentWeather: WeatherResponse?, val forecast: ForecastResponse?)