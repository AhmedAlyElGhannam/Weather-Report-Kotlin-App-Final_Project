package com.example.mvvm.model.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRemoteDataSourceImpl(var IProductService: IProductService) : IProductRemoteDataSource {

    override suspend fun makeNetworkCall() :List<Product>?
    {
        return withContext(Dispatchers.IO) {
            IProductService.getProducts()?.body()?.products
        }
    }
}