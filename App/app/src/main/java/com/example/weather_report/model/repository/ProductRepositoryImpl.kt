package com.example.mvvm.model.repo

import com.example.mvvm.model.local.IProductLocalDataSource
import com.example.mvvm.model.pojo.Product
import com.example.mvvm.model.remote.ProductRemoteDataSourceImpl


class ProductRepositoryImpl private constructor(
    private val local : IProductLocalDataSource,
    private val remote : ProductRemoteDataSourceImpl
) : IProductRepository {
    companion object {
        private var repo : ProductRepositoryImpl? = null

        fun getInstance(_local : IProductLocalDataSource, _remote : ProductRemoteDataSourceImpl) : ProductRepositoryImpl {
            return repo ?: synchronized(this) {
                val temp = ProductRepositoryImpl(_local, _remote)
                repo = temp
                temp
            }
        }
    }

    override suspend fun getProducts(flag : Boolean) : List<Product>? {
        return if (flag) {
            remote.makeNetworkCall()
        }
        else {
            local.getAllProducts()
        }
    }

    override suspend fun insertSingleProduct(product : Product) {
        local.insertProduct(product)
    }

    override suspend fun removeSingleProduct(product: Product) {
        local.removeProduct(product)
    }
}

