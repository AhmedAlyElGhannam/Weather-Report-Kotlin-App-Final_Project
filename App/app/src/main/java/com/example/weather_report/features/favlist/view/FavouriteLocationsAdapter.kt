package com.example.weather_report.features.favlist.view

import android.annotation.SuppressLint
import android.content.Context
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
        holder.binding.dailyWeatherIcon.setAnimation(
            when (currObj.currentWeather?.weather?.get(0)?.main) {
                "Thunderstorm" -> R.raw.thunderstorm
                "Drizzle" -> R.raw.drizzle
                "Rain" -> R.raw.rainy
                "Snow" -> R.raw.snowy
                "Mist" -> R.raw.misty
                "Smoke" -> R.raw.misty
                "Haze" -> R.raw.misty
                "Dust" -> R.raw.misty
                "Fog" -> R.raw.misty
                "Sand" -> R.raw.misty
                "Ash" -> R.raw.misty
                "Squall" -> R.raw.misty
                "Tornado" -> R.raw.misty
                "Clear" -> R.raw.sunny
                "Clouds" -> R.raw.cloudy
                else -> R.raw.sunny
            }
        )

        holder.binding.locName.text = currObj.location.name
        holder.binding.locTempTextView.text = "${currObj.currentWeather?.main?.temp?.let {
            appliedSettings.convertToTempUnit(it)
        }}°${appliedSettings.getTempUnit().symbol}"

        holder.binding.item.setOnClickListener {
            onItemClick.invoke(currObj)
        }

        holder.binding.favButton.setOnClickListener {
            onRemoveClick.invoke(currObj.location.id)
        }
    }

    inner class FavouriteLocationViewHolder(var binding: ItemFavouriteLocationBinding) : RecyclerView.ViewHolder(binding.root)
}