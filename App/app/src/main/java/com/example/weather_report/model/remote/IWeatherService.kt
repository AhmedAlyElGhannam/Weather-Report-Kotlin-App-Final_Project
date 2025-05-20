package com.example.weather_report.model.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IWeatherService {
    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("units") units : String
    ) : Response<ForecastResponse?>?

    @GET("current")
    suspend fun getWeather(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("units") units : String
    ) : Response<WeatherResponse?>?
}