package com.example.weather_report.features.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_report.R
import com.example.weather_report.databinding.ItemDailyWeatherBinding
import com.example.weather_report.model.pojo.sub.ForecastItem
import com.example.weather_report.utils.settings.AppliedSystemSettings
import com.example.weather_report.utils.diff.ForecastItemDiffUtil
import java.text.NumberFormat

class DailyWeatherForecastAdapter:
    ListAdapter<ForecastItem, DailyWeatherForecastAdapter.DailyWeatherViewHolder>(
        ForecastItemDiffUtil()
    ) {
    lateinit var context: Context

    private lateinit var binding: ItemDailyWeatherBinding

    private lateinit var appliedSettings: AppliedSystemSettings

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        context = parent.context
        appliedSettings = AppliedSystemSettings.getInstance(context)
        binding = ItemDailyWeatherBinding.inflate(inflater, parent, false)
        return DailyWeatherViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        val currentObj = getItem(position)
        currentObj?.let { item ->
            holder.binding.dailyTempTextView.text = "${formatNumberAccordingToLocale(appliedSettings.convertToTempUnit(item.main.temp), context)}°${appliedSettings.getTempUnit().getLocalizedSymbol(context)}"
            holder.binding.dailyDayTextView.text = item.dt_txt.substringBefore(" ")
            holder.binding.dailyWeatherIcon.setImageResource(
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
            // display nothing
        }
    }

    private fun formatNumberAccordingToLocale(number: Number, context: Context): String {
        val locale = context.resources.configuration.locales.get(0)
        val formatter = NumberFormat.getInstance(locale)
        return formatter.format(number)
    }

    class DailyWeatherViewHolder(var binding: ItemDailyWeatherBinding) : RecyclerView.ViewHolder(binding.root)
}