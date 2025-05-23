package com.example.weather_report.features.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeScreenFragment : Fragment() {
    lateinit var binding: FragmentHomeScreenBinding
    lateinit var hourlyWeatherAdapter: HourlyWeatherAdapter
    lateinit var dailyWeatherForecastAdapter: DailyWeatherForecastAdapter
    private var hasFetchedWeatherData = false
    private var hasFetchedForecastData = false
    private var weather: WeatherResponse? = null
    private var forecast: ForecastResponse? = null
    val formatUnixTime: (Long) -> String = { unixTime ->
        val date = Date(unixTime * 1000)
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        format.timeZone = TimeZone.getDefault()
        format.format(date)
    }
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

        // layout manager for hourly
        binding.hourlyWeatherRecyclerView.hasFixedSize()
        val layoutManagerHourly : LinearLayoutManager = LinearLayoutManager(requireContext())
        layoutManagerHourly.orientation = RecyclerView.HORIZONTAL
        binding.hourlyWeatherRecyclerView.layoutManager = layoutManagerHourly

        // layout manager for daily
        binding.dailyWeatherRecyclerView.hasFixedSize()
        val layoutManagerDaily : LinearLayoutManager = LinearLayoutManager(requireContext())
        layoutManagerDaily.orientation = RecyclerView.HORIZONTAL
        binding.dailyWeatherRecyclerView.layoutManager = layoutManagerDaily

        // adapter for hourly
        hourlyWeatherAdapter = HourlyWeatherAdapter()
        hourlyWeatherAdapter.submitList(listOf())
        binding.hourlyWeatherRecyclerView.adapter = hourlyWeatherAdapter

        // adapter for daily
        dailyWeatherForecastAdapter = DailyWeatherForecastAdapter()
        dailyWeatherForecastAdapter.submitList(listOf())
        binding.dailyWeatherRecyclerView.adapter = dailyWeatherForecastAdapter

        setupObservers()
    }

    private fun setupObservers() {
        mainActivityViewModel.weatherResponse
            .observe(viewLifecycleOwner) {
                if (!hasFetchedWeatherData) {
                weather = mainActivityViewModel.weatherResponse.value
                hasFetchedWeatherData = true

                weather?.let {
                    updateWeatherUI(it)
                    updateExtraInfo(it)
                }
            }
        }

        mainActivityViewModel.forecastResponse
            .observe(viewLifecycleOwner) {
                if (!hasFetchedForecastData) {
                    forecast = mainActivityViewModel.forecastResponse.value
                    hasFetchedForecastData = true

                    forecast?.let {
                        updateForecastUI(it)
                    }
                }
            }

        mainActivityViewModel.hourlyWeatherItemsList
            .observe(viewLifecycleOwner) { filteredList ->
                Log.i("TAG", "Observing hourly list: ${filteredList?.size}")
                hourlyWeatherAdapter.submitList(filteredList)
            }

        mainActivityViewModel.dailyWeatherItemsList.observe(viewLifecycleOwner) { dailyList ->
            dailyList?.let {
                dailyWeatherForecastAdapter.submitList(it)
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

    @SuppressLint("SetTextI18n")
    private fun updateExtraInfo(weather: WeatherResponse) {
        binding.windSpeedTxt.text = "${weather.wind.speed}${ChosenDataUnits.speedUnit}"
        binding.sunsetTxt.text = formatUnixTime.invoke(weather.sys.sunset)
        binding.pressureTxt.text = "${weather.main.pressure}${ChosenDataUnits.pressureUnit}"
        binding.humidityTxt.text = "${weather.main.humidity}%"
        binding.sunriseTxt.text = formatUnixTime.invoke(weather.sys.sunrise)
        binding.cloudCoverageTxt.text = "${weather.clouds.all}%"
    }

    private fun updateForecastUI(forecast: ForecastResponse) {

    }
}