package com.example.weather_report.model.pojo.sub

import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("3h") val volume: Double
)