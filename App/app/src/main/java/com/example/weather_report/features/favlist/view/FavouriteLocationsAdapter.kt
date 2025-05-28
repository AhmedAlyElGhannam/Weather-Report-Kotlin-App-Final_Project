package com.example.weather_report.features.favlist.view

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_report.R
import com.example.weather_report.databinding.ItemFavouriteLocationBinding
import com.example.weather_report.model.pojo.entity.LocationWithWeather
import com.example.weather_report.utils.settings.AppliedSystemSettings
import com.example.weather_report.utils.diff.FavouriteLocationDiffUtil
import java.text.NumberFormat

class FavouriteLocationsAdapter(
    private val onItemClick: (LocationWithWeather) -> Unit,
    private val onRemoveClick: (String) -> Unit,
) : ListAdapter<LocationWithWeather, FavouriteLocationsAdapter.FavouriteLocationViewHolder>(
    FavouriteLocationDiffUtil()
) {

    lateinit var context: Context
    lateinit var appliedSettings: AppliedSystemSettings
    lateinit var binding: ItemFavouriteLocationBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteLocationViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        context = parent.context
        appliedSettings = AppliedSystemSettings.getInstance(context)
        binding = ItemFavouriteLocationBinding.inflate(inflater, parent, false)
        return FavouriteLocationViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FavouriteLocationViewHolder, position: Int) {
        val currObj = getItem(position)
        if (currObj.location.isCurrent) {
            holder.binding.markedIcon.visibility = View.VISIBLE
        }

        val animationRes: Int
        val condition: String

        when (currObj.currentWeather?.weather?.get(0)?.main) {
            "Thunderstorm" -> {
                animationRes = R.raw.thunderstorm
                condition = context.getString(R.string.condition_thunderstorm)
            }
            "Drizzle" -> {
                animationRes = R.raw.drizzle
                condition = context.getString(R.string.condition_drizzle)
            }
            "Rain" -> {
                animationRes = R.raw.rainy
                condition = context.getString(R.string.condition_rain)
            }
            "Snow" -> {
                animationRes = R.raw.snowy
                condition = context.getString(R.string.condition_snow)
            }
            "Mist" -> {
                animationRes = R.raw.misty
                condition = context.getString(R.string.condition_mist)
            }
            "Smoke" -> {
                animationRes = R.raw.misty
                condition = context.getString(R.string.condition_smoke)
            }
            "Haze" -> {
                animationRes = R.raw.misty
                condition = context.getString(R.string.condition_haze)
            }
            "Dust" -> {
                animationRes = R.raw.misty
                condition = context.getString(R.string.condition_dust)
            }
            "Fog" -> {
                animationRes = R.raw.misty
                condition = context.getString(R.string.condition_fog)
            }
            "Sand" -> {
                animationRes = R.raw.misty
                condition = context.getString(R.string.condition_sand)
            }
            "Ash" -> {
                animationRes = R.raw.misty
                condition = context.getString(R.string.condition_ash)
            }
            "Squall" -> {
                animationRes = R.raw.misty
                condition = context.getString(R.string.condition_squall)
            }
            "Tornado" -> {
                animationRes = R.raw.misty
                condition = context.getString(R.string.condition_tornado)
            }
            "Clear" -> {
                animationRes = R.raw.sunny
                condition = context.getString(R.string.condition_clear)
            }
            "Clouds" -> {
                animationRes = R.raw.cloudy
                condition = context.getString(R.string.condition_clouds)
            }
            else -> {
                animationRes = R.raw.sunny
                condition = context.getString(R.string.condition_clear)
            }
        }


        holder.binding.dailyWeatherIcon.setAnimation(animationRes)

        holder.binding.locName.text = currObj.location.name
        holder.binding.locTempTextView.text = "${
            currObj!!.currentWeather?.main?.let {
                appliedSettings.convertToTempUnit(
                    it.temp)
            }?.let { formatNumberAccordingToLocale(it, context) }
        }°${appliedSettings.getTempUnit().getLocalizedSymbol(context)}"

        holder.binding.item.setOnClickListener {
            onItemClick.invoke(currObj)
        }

        holder.binding.favButton.setOnClickListener {
            onRemoveClick.invoke(currObj.location.id)
        }
    }

    private fun formatNumberAccordingToLocale(number: Number, context: Context): String {
        val locale = context.resources.configuration.locales.get(0)
        val formatter = NumberFormat.getInstance(locale)
        return formatter.format(number)
    }

    inner class FavouriteLocationViewHolder(var binding: ItemFavouriteLocationBinding) : RecyclerView.ViewHolder(binding.root)
}