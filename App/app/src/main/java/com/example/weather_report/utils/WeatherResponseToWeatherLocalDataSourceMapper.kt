package com.example.weather_report.utils

import com.example.weather_report.model.pojo.CurrentWeather
import com.example.weather_report.model.pojo.WeatherResponse


object WeatherResponseToWeatherLocalDataSourceMapper {

    fun WeatherResponse.toCurrentWeather(): CurrentWeather {
        return CurrentWeather(
            id = this.id,
            coord = this.coord,
            weather = this.weather,
            base = this.base,
            main = this.main,
            visibility = this.visibility,
            wind = this.wind,
            clouds = this.clouds,
            rain = null,
            dt = this.dt,
            sys = this.sys,
            timezone = this.timezone,
            name = this.name,
            cod = this.cod
        )
    }

    fun CurrentWeather.toWeatherResponse(): WeatherResponse {
        return WeatherResponse(
            coord = this.coord,
            weather = this.weather,
            base = this.base,
            main = this.main,
            visibility = this.visibility,
            wind = this.wind,
            clouds = this.clouds,
            dt = this.dt,
            sys = this.sys,
            timezone = this.timezone,
            id = this.id,
            name = this.name,
            cod = this.cod
        )
    }
}