package com.example.weather_report.features.details.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_report.R
import com.example.weather_report.contracts.WeatherDetailsContract
import com.example.weather_report.databinding.FragmentHomeScreenBinding
import com.example.weather_report.features.details.viewmodel.WeatherDetailsViewModel
import com.example.weather_report.features.details.viewmodel.WeatherDetailsViewModelFactory
import com.example.weather_report.features.home.view.DailyWeatherForecastAdapter
import com.example.weather_report.features.home.view.HourlyWeatherAdapter
import com.example.weather_report.model.local.LocalDB
import com.example.weather_report.model.local.LocalDataSourceImpl
import com.example.weather_report.model.pojo.response.WeatherResponse
import com.example.weather_report.model.remote.IWeatherService
import com.example.weather_report.model.remote.RetrofitHelper
import com.example.weather_report.model.remote.WeatherAndForecastRemoteDataSourceImpl
import com.example.weather_report.model.repository.WeatherRepositoryImpl
import com.example.weather_report.utils.settings.AppliedSystemSettings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class WeatherDetailsFragment
    : Fragment(), WeatherDetailsContract.View {
    lateinit var binding: FragmentHomeScreenBinding
    lateinit var hourlyWeatherAdapter: HourlyWeatherAdapter
    lateinit var dailyWeatherForecastAdapter: DailyWeatherForecastAdapter
    private var weather: WeatherResponse? = null
    private val weatherDetailsViewModel: WeatherDetailsViewModel by activityViewModels {
        WeatherDetailsViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                WeatherAndForecastRemoteDataSourceImpl(
                    RetrofitHelper.retrofit.create(IWeatherService::class.java)),
                LocalDataSourceImpl(LocalDB.getInstance(requireContext()).weatherDao())
            )
        )
    }

    private val appliedSettings by lazy { AppliedSystemSettings.getInstance(requireContext()) }

    private val formatUnixTime: (Long) -> String = { unixTime ->
        val date = Date(unixTime * 1000)
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        format.timeZone = TimeZone.getDefault()
        format.format(date)
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

        setupAdaptersAndRVs()
        setupObservers()
        onSwipeToRefreshData()
    }

    override fun onSwipeToRefreshData() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            weatherDetailsViewModel.refreshLocationData()
        }
    }

    override fun setupAdaptersAndRVs() {
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
    }

    override fun setupObservers() {
        weatherDetailsViewModel.weatherResponse
            .observe(viewLifecycleOwner) {
                weather = weatherDetailsViewModel.weatherResponse.value
                weather?.let {
                    updateWeatherUI()
                    updateExtraInfo()
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }

        weatherDetailsViewModel.hourlyWeatherItemsList
            .observe(viewLifecycleOwner) { filteredList ->
                Log.i("TAG", "Observing hourly list: ${filteredList?.size}")
                hourlyWeatherAdapter.submitList(filteredList)
            }

        weatherDetailsViewModel.dailyWeatherItemsList.observe(viewLifecycleOwner) { dailyList ->
            dailyList?.let {
                dailyWeatherForecastAdapter.submitList(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun updateWeatherUI() {
        binding.weatherImg.setAnimation(
            when(weather!!.weather[0].main) {
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

        Log.i("TAG", "Logging temperature values for my sanity!")
        Log.i("TAG", "max = ${weather!!.main.temp_max}")
        Log.i("TAG", "min = ${weather!!.main.temp_min}")
        Log.i("TAG", "avg = ${weather!!.main.temp}")
        Log.i("TAG", "feel = ${weather!!.main.feels_like}")

        // weather condition (maybe do a when-else and take from string resources)
        binding.weatherConditionTxt.text = weather!!.weather[0].main

        // (maybe do a when-else and take from string resources)
        binding.locationTxt.text = weather!!.name

        binding.highLowTempTxt.text = "↑ ${appliedSettings.convertToTempUnit(weather!!.main.temp_max)}°${appliedSettings.getTempUnit().symbol} ↓ ${appliedSettings.convertToTempUnit(weather!!.main.temp_min)}°${appliedSettings.getTempUnit().symbol}"

        binding.tempTxt.text = "${appliedSettings.convertToTempUnit(weather!!.main.temp)}°${appliedSettings.getTempUnit().symbol}"

        binding.feelslikeTempTxt.text = "Feels Like ${appliedSettings.convertToTempUnit(weather!!.main.feels_like)}°${appliedSettings.getTempUnit().symbol}"
    }

    @SuppressLint("SetTextI18n")
    override fun updateExtraInfo() {
        binding.windSpeedTxt.text = "${appliedSettings.convertToSpeedUnit(weather!!.wind.speed)}${appliedSettings.getSpeedUnit().symbol}"
        binding.sunsetTxt.text = formatUnixTime.invoke(weather!!.sys.sunset)
        binding.pressureTxt.text = "${appliedSettings.convertToPressureUnit(weather!!.main.pressure.toDouble())}${appliedSettings.getPressureUnit().symbol}"
        binding.humidityTxt.text = "${weather!!.main.humidity}%"
        binding.sunriseTxt.text = formatUnixTime.invoke(weather!!.sys.sunrise)
        binding.cloudCoverageTxt.text = "${weather!!.clouds.all}%"
    }
}