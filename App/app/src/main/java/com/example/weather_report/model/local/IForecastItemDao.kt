package com.example.weather_report.model.local

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
    suspend fun insertAllForecastItems(list : List<ForecastItem>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSingleForecastItem(forecastItem: ForecastItem)

    @Delete
    suspend fun deleteSingleForecastItem(forecastItem: ForecastItem)

    @Query("DELETE FROM Forecast_Table")
    suspend fun deleteAllForecastItems()
}