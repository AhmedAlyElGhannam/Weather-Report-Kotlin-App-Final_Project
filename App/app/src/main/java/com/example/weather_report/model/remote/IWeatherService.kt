package com.example.weather_report.model.remote

import com.example.weather_report.model.pojo.ForecastResponse
import com.example.weather_report.model.pojo.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IWeatherService {
    @GET("forecast/hourly")
    suspend fun getForecast(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("units") units : String,
        @Query("lang") lang: String
    ) : Response<ForecastResponse?>?

    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("units") units : String,
        @Query("lang") lang: String
    ) : Response<WeatherResponse?>?
}