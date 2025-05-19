package com.example.mvvm.model.local


interface IProductLocalDataSource {
    suspend fun insertProduct(product : Product)
    suspend fun removeProduct(product : Product)
    suspend fun getAllProducts() : List<Product>
}