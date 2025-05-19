package com.example.mvvm.model.repo

import com.example.mvvm.model.pojo.Product

interface IProductRepository {
    suspend fun getProducts(flag : Boolean) : List<Product>?
    suspend fun insertSingleProduct(product : Product)
    suspend fun removeSingleProduct(product: Product)
}