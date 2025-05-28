package com.example.weather_report.contracts

import com.example.weather_report.model.pojo.entity.LocationWithWeather

interface FavouriteLocationsContract {
    interface View {
        fun onAddNewLocationClickListener()
        fun setupRecyclerView()
        fun setupObservers()

    }
    interface ViewModel {
        fun loadFavouriteLocations()
        fun addFavourite(lat: Double, lon: Double, name: String)
        fun removeFavourite(locationId: String)
    }
    interface Model {
        suspend fun getFavouriteLocationsWithWeather():  List<LocationWithWeather>
        suspend fun addFavouriteLocation(lat: Double, lon: Double, name: String): Boolean
        suspend fun removeFavouriteLocation(locationId: String)
    }
}