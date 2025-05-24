package com.example.weather_report.features.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_report.MainActivityViewModel
import com.example.weather_report.features.home.viewmodel.HomeScreenViewModel
import com.example.weather_report.R
import com.example.weather_report.databinding.FragmentHomeScreenBinding
import com.example.weather_report.features.home.viewmodel.HomeScreenViewModelFactory
import com.example.weather_report.model.local.CityLocalDataSourceImpl
import com.example.weather_report.model.local.CurrentWeatherLocalDataSourceImpl
import com.example.weather_report.model.local.ForecastItemLocalDataSourceImpl
import com.example.weather_report.model.local.LocalDB
import com.example.weather_report.model.pojo.ForecastResponse
import com.example.weather_report.model.pojo.WeatherResponse
import com.example.weather_report.model.remote.IWeatherService
import com.example.weather_report.model.remote.RetrofitHelper
import com.example.weather_report.model.remote.WeatherAndForecastRemoteDataSourceImpl
import com.example.weather_report.model.repository.WeatherRepositoryImpl
import com.example.weather_report.utils.ChosenDataUnits
import com.example.weather_report.utils.Units

class HomeScreenFragment : Fragment() {
    lateinit var binding: FragmentHomeScreenBinding
    lateinit var hourlyWeatherAdapter: HourlyWeatherAdapter
    private var hasFetchedWeatherData = false
    private var hasFetchedForecastData = false
    private var weather: WeatherResponse? = null
    private var forecast: ForecastResponse? = null
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val homeViewModel : HomeScreenViewModel by viewModels {
        HomeScreenViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                CityLocalDataSourceImpl(LocalDB.getInstance(requireContext()).getCityDao()),
                ForecastItemLocalDataSourceImpl(LocalDB.getInstance(requireContext()).getForecastItemDao()),
                CurrentWeatherLocalDataSourceImpl(LocalDB.getInstance(requireContext()).getCurrentWeatherDao()),
                WeatherAndForecastRemoteDataSourceImpl(
                    RetrofitHelper.retrofit.create(
                        IWeatherService::class.java))
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.hourlyWeatherRecyclerView.hasFixedSize()
        val layoutManagerHourly : LinearLayoutManager = LinearLayoutManager(requireContext())
        layoutManagerHourly.orientation = RecyclerView.HORIZONTAL
        binding.hourlyWeatherRecyclerView.layoutManager = layoutManagerHourly

        hourlyWeatherAdapter = HourlyWeatherAdapter()
        hourlyWeatherAdapter.submitList(listOf())
        binding.hourlyWeatherRecyclerView.adapter = hourlyWeatherAdapter

        setupObservers()
    }

    private fun setupObservers() {
        mainActivityViewModel.weatherResponse
            .observe(viewLifecycleOwner) {
                if (!hasFetchedWeatherData) {
                weather = mainActivityViewModel.weatherResponse.value
                hasFetchedWeatherData = true

                weather?.let { it1 -> updateWeatherUI(it1) }
            }
        }

        mainActivityViewModel.forecastResponse
            .observe(viewLifecycleOwner) {
                if (!hasFetchedForecastData) {
                    forecast = mainActivityViewModel.forecastResponse.value
                    hasFetchedForecastData = true

                    forecast?.let { it1 ->
                        updateForecastUI(it1)
                        hourlyWeatherAdapter.submitList(it1.list)
                    }
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun updateWeatherUI(weather: WeatherResponse) {
        binding.weatherImg.setAnimation(
            when(weather.weather[0].main) {
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

        binding.weatherConditionTxt.text = weather.weather[0].main

        binding.locationTxt.text = weather.name

        binding.highLowTempTxt.text = "↑ ${weather.main.temp_max}°${ChosenDataUnits.tempUnit} ↓ ${weather.main.temp_min}°${ChosenDataUnits.tempUnit}"

        binding.tempTxt.text = "${weather.main.temp}°${ChosenDataUnits.tempUnit}"

        binding.feelslikeTempTxt.text = "Feels Like ${weather.main.feels_like}°${ChosenDataUnits.tempUnit}"
    }

    private fun updateForecastUI(forecast: ForecastResponse) {

    }
}