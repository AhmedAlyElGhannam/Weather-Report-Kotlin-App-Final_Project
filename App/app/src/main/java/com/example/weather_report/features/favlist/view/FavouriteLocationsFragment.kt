package com.example.weather_report.features.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_report.MainActivityViewModelFactory
import com.example.weather_report.databinding.FragmentFavouriteLocationsBinding
import com.example.weather_report.features.favlist.view.FavouriteLocationsAdapter
import com.example.weather_report.features.favlist.viewmodel.FavouriteLocationsViewModel
import com.example.weather_report.model.local.LocalDB
import com.example.weather_report.model.local.LocalDataSourceImpl
import com.example.weather_report.model.remote.IWeatherService
import com.example.weather_report.model.remote.RetrofitHelper
import com.example.weather_report.model.remote.WeatherAndForecastRemoteDataSourceImpl
import com.example.weather_report.model.repository.WeatherRepositoryImpl

class FavouriteLocationsFragment : Fragment() {

    private var _binding: FragmentFavouriteLocationsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavouriteLocationsViewModel by viewModels {
        MainActivityViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                WeatherAndForecastRemoteDataSourceImpl(
                    RetrofitHelper.retrofit.create(IWeatherService::class.java)),
                LocalDataSourceImpl(LocalDB.getInstance(requireContext()).weatherDao())
            )
        )
    }

    private lateinit var adapter: FavouriteLocationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteLocationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        viewModel.loadFavoriteLocations()
    }

    private fun setupRecyclerView() {
        adapter = FavouriteLocationsAdapter(
            onItemClick = { location ->
                // Handle item click - navigate to details or update current location
                // You'll need to implement this based on your navigation structure
                findNavController().navigate(
                    FavouriteLocationsFragmentDirections.actionFavouriteLocationsFragmentToHomeFragment(
                        location.location.latitude,
                        location.location.longitude
                    )
                )
            },
            onRemoveClick = { locationId ->
                viewModel.removeFavorite(locationId)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavouriteLocationsFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.favoriteLocations.observe(viewLifecycleOwner, Observer { locations ->
            adapter.submitList(locations)
            binding.emptyState.visibility = if (locations.isEmpty()) View.VISIBLE else View.GONE
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                // Show error message (you can use a Toast or Snackbar)
                // For now, we'll just log it
                println("Error: $it")
                viewModel.errorMessage.value = null // Reset error message
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}