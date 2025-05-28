package com.example.weather_report.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weather_report.model.pojo.entity.CurrentWeatherEntity
import com.example.weather_report.model.pojo.entity.ForecastWeatherEntity
import com.example.weather_report.model.pojo.entity.LocationEntity

@Database(entities = [LocationEntity::class, CurrentWeatherEntity::class, ForecastWeatherEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class LocalDB: RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE : LocalDB? = null
        fun getInstance(context: Context) : LocalDB {
            return INSTANCE ?: synchronized(this){
                val temp= Room.databaseBuilder(context.applicationContext, LocalDB::class.java , "local_db").fallbackToDestructiveMigration().build()
                INSTANCE= temp
                temp
            }
        }
    }
}