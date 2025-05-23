package com.example.weather_report.features.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
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

class HomeScreenFragment : Fragment() {
    lateinit var binding: FragmentHomeScreenBinding
    lateinit var vmFactory : HomeScreenViewModelFactory
    lateinit var homeViewModel : HomeScreenViewModel :

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*******************************************************/
        vmFactory = HomeScreenViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                CityLocalDataSourceImpl(LocalDB.getInstance(requireContext()).getCityDao()),
                ForecastItemLocalDataSourceImpl(LocalDB.getInstance(requireContext()).getForecastItemDao()),
                CurrentWeatherLocalDataSourceImpl(LocalDB.getInstance(requireContext()).getCurrentWeatherDao()),
                WeatherAndForecastRemoteDataSourceImpl(
                    RetrofitHelper.retrofit.create(
                        IWeatherService::class.java))
            )
        )

        homeViewModel = ViewModelProvider(this, vmFactory)[HomeScreenViewModel::class.java]

        /*******************************************************/

        setupObservers()
        // Trigger data fetch if location is available
        arguments?.getDoubleArray("location")?.let {
            homeViewModel.fetchWeatherData(it[0], it[1])
            homeViewModel.fetchForecastData(it[0], it[1])
        }
    }

    private fun setupObservers() {
        homeViewModel.weatherResponse.observe(viewLifecycleOwner) { weather ->
            weather?.let { updateWeatherUI(it) }
        }
        homeViewModel.forecastResponse.observe(viewLifecycleOwner) { forecast ->
            forecast?.let { updateForecastUI(it) }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateWeatherUI(weather: WeatherResponse) {
//        binding.tempTxt.text = "${weather.main.temp}°C"
//        binding.weatherConditionTxt.text = weather.weather.firstOrNull()?.main
//        binding.locationTxt.text = weather.name

        binding.weatherImg.setAnimation(
            when(weather.weather[0].description) {
                "clear sky" -> R.raw.sunny
                "rain" -> R.raw.rainy
                "rain shower" -> R.raw.rainy
                "thunderstorm" -> R.raw.thunderstorm
                "snow" -> R.raw.snowy
                "mist" -> R.raw.misty
                else -> R.raw.cloudy
            }
        )

        binding.weatherConditionTxt.text = when(weather.weather[0].description) {
            "clear sky" -> "Clear Sky"
            "rain" -> "Rainy"
            "rain shower" -> "Rainy Shower"
            "thunderstorm" -> "Thunderstorm"
            "snow" -> "Snowy"
            "mist" -> "Misty"
            "few clouds" -> "Few Clouds"
            "scattered clouds" -> "Scattered Clouds"
            "broken clouds" -> "Broken Clouds"
            else -> "Unknown"
        }

        binding.dateTxt.text = weather.main.

    }

    private fun updateForecastUI(forecast: ForecastResponse) {

    }
}