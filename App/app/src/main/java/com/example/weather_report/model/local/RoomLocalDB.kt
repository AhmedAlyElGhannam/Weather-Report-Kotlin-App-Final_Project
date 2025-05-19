package com.example.mvvm.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Product::class) , version = 1)
abstract class RoomLocalDB: RoomDatabase() {
    abstract fun getProductDao():ProductDao

    companion object{
        @Volatile
        private var INSTANCE:RoomLocalDB? = null
        fun getInstance(context: Context):RoomLocalDB{
            return INSTANCE ?: synchronized(this){
                val temp= Room.databaseBuilder(context.applicationContext , RoomLocalDB::class.java , "Products_Table").build()
                INSTANCE= temp
                temp
            }
        }
    }
}