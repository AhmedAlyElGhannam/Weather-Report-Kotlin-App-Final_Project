package com.example.mvvm.model.remote

import retrofit2.Response
import retrofit2.http.GET

interface IProductService {
    @GET("products")
    suspend fun getProducts(): Response<ProductResponse?>?
}