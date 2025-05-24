package com.example.weather_report.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.weather_report.model.pojo.ForecastItem

class ForecastItemDiffUtil : DiffUtil.ItemCallback<ForecastItem>() {
    override fun areItemsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean
    {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean
    {
        return oldItem == newItem
    }
}