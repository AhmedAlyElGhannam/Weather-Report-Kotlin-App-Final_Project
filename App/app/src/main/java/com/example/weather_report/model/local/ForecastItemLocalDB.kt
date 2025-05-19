package com.example.mvvm.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weather_report.model.pojo.ForecastItem

@Database(entities = arrayOf(ForecastItem::class) , version = 1)
abstract class ForecastItemLocalDB: RoomDatabase() {
    abstract fun getForecastItemDao() : IForecastItemDao

    companion object {
        @Volatile
        private var INSTANCE:ForecastItemLocalDB? = null
        fun getInstance(context: Context) : ForecastItemLocalDB {
            return INSTANCE ?: synchronized(this){
                val temp= Room.databaseBuilder(context.applicationContext, ForecastItemLocalDB::class.java , "Forecast_Table").build()
                INSTANCE= temp
                temp
            }
        }
    }
}