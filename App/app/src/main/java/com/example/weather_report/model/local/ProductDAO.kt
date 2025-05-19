package com.example.mvvm.model.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductDao {
    @Query("SELECT * FROM Products_Table")
    suspend fun getAllProducts():List<Product>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllProducts(list : List<Product>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSingleProduct(product : Product)

    @Delete
    suspend fun deleteSingleProduct(product: Product)
}