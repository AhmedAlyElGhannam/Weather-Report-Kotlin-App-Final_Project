package com.example.weather_report.features.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_report.R
import com.example.weather_report.databinding.ItemHourlyWeatherBinding
import com.example.weather_report.model.pojo.sub.ForecastItem
import com.example.weather_report.utils.diff.ForecastItemDiffUtil

class HourlyWeatherAdapter:
    ListAdapter<ForecastItem, HourlyWeatherAdapter.HourlyWeatherViewHolder>(ForecastItemDiffUtil()) {
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

        val iconRes: Int
        val condition: String

        when (currentObj.weather[0].main) {
            "Thunderstorm" -> {
                iconRes = R.drawable.ic_thunderstormy
                condition = context.getString(R.string.condition_thunderstorm)
            }
            "Drizzle" -> {
                iconRes = R.drawable.ic_drizzley
                condition = context.getString(R.string.condition_drizzle)
            }
            "Rain" -> {
                iconRes = R.drawable.ic_rainy
                condition = context.getString(R.string.condition_rain)
            }
            "Snow" -> {
                iconRes = R.drawable.ic_snowy
                condition = context.getString(R.string.condition_snow)
            }
            "Mist" -> {
                iconRes = R.drawable.ic_misty
                condition = context.getString(R.string.condition_mist)
            }
            "Smoke" -> {
                iconRes = R.drawable.ic_misty
                condition = context.getString(R.string.condition_smoke)
            }
            "Haze" -> {
                iconRes = R.drawable.ic_misty
                condition = context.getString(R.string.condition_haze)
            }
            "Dust" -> {
                iconRes = R.drawable.ic_misty
                condition = context.getString(R.string.condition_dust)
            }
            "Fog" -> {
                iconRes = R.drawable.ic_misty
                condition = context.getString(R.string.condition_fog)
            }
            "Sand" -> {
                iconRes = R.drawable.ic_misty
                condition = context.getString(R.string.condition_sand)
            }
            "Ash" -> {
                iconRes = R.drawable.ic_misty
                condition = context.getString(R.string.condition_ash)
            }
            "Squall" -> {
                iconRes = R.drawable.ic_misty
                condition = context.getString(R.string.condition_squall)
            }
            "Tornado" -> {
                iconRes = R.drawable.ic_misty
                condition = context.getString(R.string.condition_tornado)
            }
            "Clear" -> {
                iconRes = R.drawable.ic_sunny
                condition = context.getString(R.string.condition_clear)
            }
            "Clouds" -> {
                iconRes = R.drawable.ic_cloudy
                condition = context.getString(R.string.condition_clouds)
            }
            else -> {
                iconRes = R.drawable.ic_sunny
                condition = context.getString(R.string.condition_clear)
            }
        }

        holder.binding.hourlyWeatherIcon.setImageResource(iconRes)
        holder.binding.hourlyTempTextView.text = condition
        holder.binding.hourlyTimeTextView.text = currentObj.dt_txt.substringAfter(" ")

    }

    class HourlyWeatherViewHolder(var binding: ItemHourlyWeatherBinding) : RecyclerView.ViewHolder(binding.root)
}