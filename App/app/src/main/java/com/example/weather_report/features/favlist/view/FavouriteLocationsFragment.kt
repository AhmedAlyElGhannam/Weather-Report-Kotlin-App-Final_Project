package com.example.weather_report.features.favorites

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_report.contracts.FavouriteLocationsContract
import com.example.weather_report.databinding.FragmentFavouritesBinding
import com.example.weather_report.features.details.viewmodel.WeatherDetailsViewModel
import com.example.weather_report.features.details.viewmodel.WeatherDetailsViewModelFactory
import com.example.weather_report.features.favlist.view.FavouriteLocationsAdapter
import com.example.weather_report.features.favlist.viewmodel.FavouriteLocationsViewModel
import com.example.weather_report.features.favlist.viewmodel.FavouriteLocationsViewModelFactory
import com.example.weather_report.features.mapdialog.view.MapDialog
import com.example.weather_report.model.local.LocalDB
import com.example.weather_report.model.local.LocalDataSourceImpl
import com.example.weather_report.model.remote.IWeatherService
import com.example.weather_report.model.remote.RetrofitHelper
import com.example.weather_report.model.remote.WeatherAndForecastRemoteDataSourceImpl
import com.example.weather_report.model.repository.WeatherRepositoryImpl
import com.example.weather_report.utils.callback.ISelectedCoordinatesOnMapCallback
import java.util.Locale


class FavouriteLocationsFragment
    : Fragment(),
    ISelectedCoordinatesOnMapCallback,
    FavouriteLocationsContract.View {

    private lateinit var binding: FragmentFavouritesBinding
    private lateinit var adapter: FavouriteLocationsAdapter
    private val weatherDetailsViewModel: WeatherDetailsViewModel by activityViewModels {
        WeatherDetailsViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                WeatherAndForecastRemoteDataSourceImpl(
                    RetrofitHelper.retrofit.create(IWeatherService::class.java)),
                LocalDataSourceImpl(LocalDB.getInstance(requireContext()).weatherDao())
            )
        )
    }

    private val viewModel: FavouriteLocationsViewModel by viewModels {
        FavouriteLocationsViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                WeatherAndForecastRemoteDataSourceImpl(
                    RetrofitHelper.retrofit.create(IWeatherService::class.java)),
                LocalDataSourceImpl(LocalDB.getInstance(requireContext()).weatherDao())
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        viewModel.loadFavouriteLocations()
        onAddNewLocationClickListener()
    }

    override fun onAddNewLocationClickListener() {
        binding.fabAdd.setOnClickListener {
            if (isConnectedToInternet()) {
                MapDialog(this).show(parentFragmentManager, "MapDialog")
            }
            else {
                Toast.makeText(requireContext(), "Please connect to the internet to add more locations", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun setupRecyclerView() {
        adapter = FavouriteLocationsAdapter(
            onItemClick = { locationWithWeather ->
                if (locationWithWeather.currentWeather != null && locationWithWeather.forecast != null) {
                    weatherDetailsViewModel.setFavoriteLocationData(locationWithWeather)
                    findNavController().navigate(
                        FavouriteLocationsFragmentDirections.actionFavouriteLocationsFragmentToWeatherDetailsFragment()
                    )
                } else {
                    Toast.makeText(requireContext(), "Weather data not available for this location", Toast.LENGTH_SHORT).show()
                }
            },
            onRemoveClick = { locationId ->
                AlertDialog.Builder(context)
                    .setTitle("Favourite Location")
                    .setMessage("Are you sure you want to remove this location from favourites?")
                    .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                        viewModel.removeFavourite(locationId)
                        Toast.makeText(context, "Location Removed from Favourites", Toast.LENGTH_SHORT)
                            .show()
                    })
                    .setNegativeButton("No", null)
                    .show()
            }
        )

        binding.mainRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavouriteLocationsFragment.adapter
        }
    }

    override fun setupObservers() {
        viewModel.favoriteLocations.observe(viewLifecycleOwner, Observer { locations ->
            adapter.submitList(locations)
        })
    }

    override fun onCoordinatesSelected(lat: Double, lon: Double) {

        val addresses = Geocoder(requireContext(), Locale.getDefault()).getFromLocation(lat, lon, 1)
        var cityWithCountryCode : String

        if (addresses.isNullOrEmpty()) {
            cityWithCountryCode = "Unknown"
        }
        else {
            val address = addresses[0]
            val city = address.locality ?: "Unknown"
            val country = address.countryCode
            cityWithCountryCode = "${city}, ${country}"
        }

        Log.i("TAG", "onCoordinatesSelected: ${cityWithCountryCode}")
        viewModel.addFavourite(
            lat,
            lon,
            cityWithCountryCode
        )
    }

    private fun isConnectedToInternet(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)
        }
        return networkCapabilities?.let {
            it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } ?: false
    }
}