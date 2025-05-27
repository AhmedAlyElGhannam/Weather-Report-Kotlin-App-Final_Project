package com.example.weather_report.features.favlist.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_report.model.pojo.LocationWithWeather
import com.example.weather_report.utils.FavouriteLocationDiffUtil

class FavouriteLocationsAdapter(
    private val onItemClick: (LocationWithWeather) -> Unit,
    private val onRemoveClick: (String) -> Unit
) : ListAdapter<LocationWithWeather, FavouriteLocationsAdapter.FavouriteLocationViewHolder>(
    FavouriteLocationDiffUtil()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteLocationViewHolder {
        val binding = ItemFavouriteLocationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavouriteLocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouriteLocationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FavouriteLocationViewHolder(
        private val binding: ItemFavouriteLocationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(location: LocationWithWeather) {
            binding.apply {
                locationName.text = location.location.name
                locationCoordinates.text = "${location.location.latitude}, ${location.location.longitude}"

                // Set click listeners
                root.setOnClickListener { onItemClick(location) }
                removeButton.setOnClickListener { onRemoveClick(location.location.id) }
            }
        }
    }
}