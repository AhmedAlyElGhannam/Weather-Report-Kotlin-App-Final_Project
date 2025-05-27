package com.example.weather_report.model.pojo

data class LocationWithWeather(val location: LocationEntity, val currentWeather: WeatherResponse?, val forecast: ForecastResponse?)