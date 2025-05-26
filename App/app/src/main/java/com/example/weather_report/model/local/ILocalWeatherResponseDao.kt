//package com.example.weather_report.model.local
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//
//@Dao
//interface ILocalWeatherResponseDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(weather: LocalWeatherResponse)
//
//    @Query("SELECT * FROM LocalWeatherTable WHERE cityId = :cityId")
//    suspend fun getByCityId(cityId: Int): LocalWeatherResponse?
//
//    @Query("DELETE FROM LocalWeatherTable WHERE cityId = :cityId")
//    suspend fun deleteByCityId(cityId: Int)
//}