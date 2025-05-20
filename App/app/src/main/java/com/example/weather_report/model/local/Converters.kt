package com.example.weather_report.model.local

import androidx.room.TypeConverter
import com.example.weather_report.model.pojo.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromCoordinates(coord: Coordinates): String = gson.toJson(coord)

    @TypeConverter
    fun toCoordinates(value: String): Coordinates =
        gson.fromJson(value, Coordinates::class.java)

    @TypeConverter
    fun fromMainWeather(main: MainWeather): String = gson.toJson(main)

    @TypeConverter
    fun toMainWeather(value: String): MainWeather =
        gson.fromJson(value, MainWeather::class.java)

    @TypeConverter
    fun fromClouds(clouds: Clouds): String = gson.toJson(clouds)

    @TypeConverter
    fun toClouds(value: String): Clouds =
        gson.fromJson(value, Clouds::class.java)

    @TypeConverter
    fun fromWind(wind: Wind): String = gson.toJson(wind)

    @TypeConverter
    fun toWind(value: String): Wind =
        gson.fromJson(value, Wind::class.java)

    @TypeConverter
    fun fromRain(rain: Rain?): String = gson.toJson(rain)

    @TypeConverter
    fun toRain(value: String): Rain? =
        gson.fromJson(value, Rain::class.java)

    @TypeConverter
    fun fromSys(sys: Sys): String = gson.toJson(sys)

    @TypeConverter
    fun toSys(value: String): Sys =
        gson.fromJson(value, Sys::class.java)

    @TypeConverter
    fun fromWeatherList(list: List<Weather>): String = gson.toJson(list)

    @TypeConverter
    fun toWeatherList(value: String): List<Weather> {
        val type = object : TypeToken<List<Weather>>() {}.type
        return gson.fromJson(value, type)
    }
}
