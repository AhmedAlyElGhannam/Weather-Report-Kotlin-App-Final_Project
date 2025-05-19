package com.example.weather_report.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weather_report.model.pojo.City
import com.example.weather_report.model.pojo.ForecastItem

@Database(entities = arrayOf(ForecastItem::class, City::class) , version = 1)
abstract class LocalDB: RoomDatabase() {
    abstract fun getForecastItemDao() : IForecastItemDao
    abstract fun getCityDao() : ICityDao

    companion object {
        @Volatile
        private var INSTANCE : LocalDB? = null
        fun getInstance(context: Context) : LocalDB {
            return INSTANCE ?: synchronized(this){
                val temp= Room.databaseBuilder(context.applicationContext, LocalDB::class.java , "local_db").build()
                INSTANCE= temp
                temp
            }
        }
    }
}