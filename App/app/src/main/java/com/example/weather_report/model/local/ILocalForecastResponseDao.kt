//package com.example.weather_report.model.local
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//
//@Dao
//interface ILocalForecastResponseDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(forecast: LocalForecastResponse)
//
//    @Query("SELECT * FROM LocalForecastTable WHERE cityId = :cityId")
//    suspend fun getByCityId(cityId: Int): LocalForecastResponse?
//
//    @Query("DELETE FROM LocalForecastTable WHERE cityId = :cityId")
//    suspend fun deleteByCityId(cityId: Int)
//}