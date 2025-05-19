package com.example.weather_report.model.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather_report.model.pojo.City

@Dao
interface ICityDao {
    @Query("SELECT * FROM City_Table")
    suspend fun getAllCities() : List<City>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSingleCity(city : City)

    @Delete
    suspend fun deleteSingleCity(city : City)
}