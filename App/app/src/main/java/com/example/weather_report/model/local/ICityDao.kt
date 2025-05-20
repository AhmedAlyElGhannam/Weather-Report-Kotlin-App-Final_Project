package com.example.weather_report.model.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weather_report.model.pojo.City

@Dao
interface ICityDao {
    @Query("SELECT * FROM City_Table")
    suspend fun getAllCities() : List<City>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSingleCity(city : City)

    @Delete
    suspend fun deleteSingleCity(city : City)

    @Query("SELECT * FROM City_Table WHERE id = :cityId")
    suspend fun getCityById(cityId: Int): City?

    @Update
    suspend fun updateCity(city: City)

    @Query("SELECT EXISTS(SELECT * FROM City_Table WHERE id = :cityId)")
    suspend fun doesCityExist(cityId: Int): Boolean
}