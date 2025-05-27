package com.example.weather_report.model.local

import androidx.room.TypeConverter
import com.example.weather_report.model.pojo.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun weatherResponseToJson(response: WeatherResponse): String = gson.toJson(response)

    @TypeConverter
    fun jsonToWeatherResponse(json: String): WeatherResponse =
        gson.fromJson(json, WeatherResponse::class.java)

    @TypeConverter
    fun forecastResponseToJson(response: ForecastResponse): String = gson.toJson(response)

    @TypeConverter
    fun jsonToForecastResponse(json: String): ForecastResponse =
        gson.fromJson(json, ForecastResponse::class.java)
    
}
