package com.example.mvvm.model.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductLocalDataSourceImpl(val dao : ProductDao) : IProductLocalDataSource{
    override suspend fun insertProduct(product: Product) {
        withContext(Dispatchers.IO) {
            dao.insertSingleProduct(product)
        }
    }

    override suspend fun removeProduct(product: Product) {
        withContext(Dispatchers.IO) {
            dao.deleteSingleProduct(product)
        }
    }

    override suspend fun getAllProducts(): List<Product> {
        return withContext(Dispatchers.IO) {
            dao.getAllProducts()
        }
    }
}