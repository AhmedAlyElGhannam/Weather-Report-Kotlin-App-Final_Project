package com.example.mvvm.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weather_report.model.local.ICityDao
import com.example.weather_report.model.pojo.City

@Database(entities = arrayOf(City::class) , version = 1)
abstract class CityLocalDB: RoomDatabase() {
    abstract fun getCityDao() : ICityDao

    companion object{
        @Volatile
        private var INSTANCE:CityLocalDB? = null
        fun getInstance(context: Context):CityLocalDB{
            return INSTANCE ?: synchronized(this){
                val temp= Room.databaseBuilder(context.applicationContext , CityLocalDB::class.java , "City_Table").build()
                INSTANCE= temp
                temp
            }
        }
    }
}