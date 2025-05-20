package com.example.weather_report.model.pojo

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "City_Table")
data class City(
    @PrimaryKey val id: Int,
    val name: String,
    val coord: Coordinates,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long,
    var isCurrLocation : Boolean = false
)