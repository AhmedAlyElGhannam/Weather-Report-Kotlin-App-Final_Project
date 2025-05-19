package com.example.weather_report.model.pojo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Products_Table")
data class Product(@PrimaryKey var id:Int ,
                   var title:String ,
                   var description:String ,
                   var price :Double ,
                   var rating:Double ,
                   var thumbnail:String): Serializable

