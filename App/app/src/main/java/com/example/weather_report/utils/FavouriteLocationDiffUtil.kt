package com.example.weather_report.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.weather_report.model.pojo.LocationWithWeather

class FavouriteLocationDiffUtil: DiffUtil.ItemCallback<LocationWithWeather>() {
    override fun areItemsTheSame(
        oldItem: LocationWithWeather,
        newItem: LocationWithWeather
    ): Boolean {
        return oldItem.location.id == newItem.location.id
    }

    override fun areContentsTheSame(
        oldItem: LocationWithWeather,
        newItem: LocationWithWeather
    ): Boolean {
        return oldItem == newItem
    }
}