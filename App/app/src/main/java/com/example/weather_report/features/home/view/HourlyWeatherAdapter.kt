package com.example.weather_report.features.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather_report.R
import com.example.weather_report.databinding.ItemHourlyWeatherBinding
import com.example.weather_report.model.pojo.ForecastItem
import com.example.weather_report.utils.HourlyWeatherDiffUtil

class HourlyWeatherAdapter:
    ListAdapter<ForecastItem, HourlyWeatherAdapter.HourlyWeatherViewHolder>(HourlyWeatherDiffUtil()) {
    lateinit var context: Context

    lateinit var binding: ItemHourlyWeatherBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        val inflater:LayoutInflater= LayoutInflater.from(parent.context)
        context= parent.context
        binding = ItemHourlyWeatherBinding.inflate(inflater, parent, false)
        return HourlyWeatherViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        val currentObj = getItem(position)
        currentObj?.let { item ->
            holder.binding.hourlyTempTextView.text = item.weather[0].main

            holder.binding.hourlyTimeTextView.text = item.dt_txt.substringAfter(" ")

            holder.binding.hourlyWeatherIcon.setImageResource(
                when(item.weather[0].main) {
                    "Thunderstorm" -> R.drawable.ic_thunderstormy
                    "Drizzle" -> R.drawable.ic_drizzley
                    "Rain" -> R.drawable.ic_rainy
                    "Snow" -> R.drawable.ic_snowy
                    "Mist" -> R.drawable.ic_misty
                    "Smoke" -> R.drawable.ic_misty
                    "Haze" -> R.drawable.ic_misty
                    "Dust" -> R.drawable.ic_misty
                    "Fog" -> R.drawable.ic_misty
                    "Sand" -> R.drawable.ic_misty
                    "Ash" -> R.drawable.ic_misty
                    "Squall" -> R.drawable.ic_misty
                    "Tornado" -> R.drawable.ic_misty
                    "Clear" -> R.drawable.ic_sunny
                    "Clouds" -> R.drawable.ic_cloudy
                    else -> R.drawable.ic_sunny
                }
            )
        } ?: run {
            holder.binding.hourlyTimeTextView.text = "--:--"
            holder.binding.hourlyWeatherIcon.setImageResource(R.drawable.ic_sunny)
            holder.binding.hourlyTempTextView.text = "--°"
        }
    }

    class HourlyWeatherViewHolder(var binding: ItemHourlyWeatherBinding) : RecyclerView.ViewHolder(binding.root)
}