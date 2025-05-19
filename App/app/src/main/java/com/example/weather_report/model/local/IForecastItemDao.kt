package com.example.mvvm.model.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather_report.model.pojo.ForecastItem

@Dao
interface IForecastItemDao {
    @Query("SELECT * FROM Forecast_Table")
    suspend fun getAllForecastItems() : List<ForecastItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllForecastItem(list : List<ForecastItem>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSingleForecastItem(forecastItem: ForecastItem)

    @Delete
    suspend fun deleteSingleForecastItem(forecastItem: ForecastItem)
}