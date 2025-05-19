package com.example.mvvm.model.remote


interface IProductRemoteDataSource {
    suspend fun makeNetworkCall() :List<Product>?
}