package com.example.weather_report.model.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weather_report.model.pojo.CurrentWeather

@Dao
interface IWeatherDao {
//    @Query("SELECT * FROM Current_Weather_Table")
//    suspend fun getAllCurrentWeather(): List<CurrentWeather>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertCurrentWeather(currentWeather: CurrentWeather)
//
//    @Delete
//    suspend fun deleteCurrentWeather(currentWeather: CurrentWeather)
//
//    @Query("DELETE FROM Current_Weather_Table")
//    suspend fun deleteAllCurrentWeather()
//
//    @Query("SELECT * FROM Current_Weather_Table WHERE id = :cityId")
//    suspend fun getCurrentWeatherByCityId(cityId: Int): CurrentWeather?
//
//    @Update
//    suspend fun updateCurrentWeather(currentWeather: CurrentWeather)
//
//    @Query("DELETE FROM Current_Weather_Table WHERE id = :cityId")
//    suspend fun deleteCurrentWeatherByCityId(cityId: Int)
//
//    @Query("SELECT COUNT(*) FROM Current_Weather_Table WHERE id = :cityId")
//    suspend fun doesCurrentWeatherExistForCity(cityId: Int): Boolean
}
