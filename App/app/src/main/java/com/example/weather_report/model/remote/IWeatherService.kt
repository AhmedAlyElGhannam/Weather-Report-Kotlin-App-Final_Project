package com.example.mvvm.model.remote

import com.example.weather_report.model.remote.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IWeatherService {
    @GET("forecast")
    suspend fun getWeather(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("units") units : String
    ) : Response<WeatherResponse?>?
}