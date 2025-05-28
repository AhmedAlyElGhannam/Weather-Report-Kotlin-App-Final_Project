package com.example.weather_report.features.details.view

import android.annotation.SuppressLint
import android.content.Context
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
import java.text.NumberFormat
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

        val animationRes: Int
        val condition: String

        when (weather!!.weather[0].main) {
            "Thunderstorm" -> {
                animationRes = R.raw.thunderstorm
                condition = getString(R.string.condition_thunderstorm)
            }
            "Drizzle" -> {
                animationRes = R.raw.drizzle
                condition = getString(R.string.condition_drizzle)
            }
            "Rain" -> {
                animationRes = R.raw.rainy
                condition = getString(R.string.condition_rain)
            }
            "Snow" -> {
                animationRes = R.raw.snowy
                condition = getString(R.string.condition_snow)
            }
            "Mist" -> {
                animationRes = R.raw.misty
                condition = getString(R.string.condition_mist)
            }
            "Smoke" -> {
                animationRes = R.raw.misty
                condition = getString(R.string.condition_smoke)
            }
            "Haze" -> {
                animationRes = R.raw.misty
                condition = getString(R.string.condition_haze)
            }
            "Dust" -> {
                animationRes = R.raw.misty
                condition = getString(R.string.condition_dust)
            }
            "Fog" -> {
                animationRes = R.raw.misty
                condition = getString(R.string.condition_fog)
            }
            "Sand" -> {
                animationRes = R.raw.misty
                condition = getString(R.string.condition_sand)
            }
            "Ash" -> {
                animationRes = R.raw.misty
                condition = getString(R.string.condition_ash)
            }
            "Squall" -> {
                animationRes = R.raw.misty
                condition = getString(R.string.condition_squall)
            }
            "Tornado" -> {
                animationRes = R.raw.misty
                condition = getString(R.string.condition_tornado)
            }
            "Clear" -> {
                animationRes = R.raw.sunny
                condition = getString(R.string.condition_clear)
            }
            "Clouds" -> {
                animationRes = R.raw.cloudy
                condition = getString(R.string.condition_clouds)
            }
            else -> {
                animationRes = R.raw.sunny
                condition = getString(R.string.condition_clear)
            }
        }

        binding.weatherImg.setAnimation(animationRes)

        Log.i("TAG", "Logging temperature values for my sanity!")
        Log.i("TAG", "max = ${weather!!.main.temp_max}")
        Log.i("TAG", "min = ${weather!!.main.temp_min}")
        Log.i("TAG", "avg = ${weather!!.main.temp}")
        Log.i("TAG", "feel = ${weather!!.main.feels_like}")

        // weather condition (maybe do a when-else and take from string resources)
        binding.weatherConditionTxt.text = condition

        // (maybe do a when-else and take from string resources)
        binding.locationTxt.text = weather!!.name

        binding.highLowTempTxt.text = "↑ ${formatNumberAccordingToLocale(appliedSettings.convertToTempUnit(weather!!.main.temp_max), requireContext())}°${appliedSettings.getTempUnit().getLocalizedSymbol(requireContext())} ↓ ${formatNumberAccordingToLocale(appliedSettings.convertToTempUnit(weather!!.main.temp_min), requireContext())}°${appliedSettings.getTempUnit().getLocalizedSymbol(requireContext())}"

        binding.tempTxt.text = "${formatNumberAccordingToLocale(appliedSettings.convertToTempUnit(weather!!.main.temp), requireContext())}°${appliedSettings.getTempUnit().getLocalizedSymbol(requireContext())}"

        binding.feelslikeTempTxt.text = "${getString(R.string.feels_like)} ${formatNumberAccordingToLocale(appliedSettings.convertToTempUnit(weather!!.main.feels_like), requireContext())}°${appliedSettings.getTempUnit().getLocalizedSymbol(requireContext())}"
    }

    @SuppressLint("SetTextI18n")
    override fun updateExtraInfo() {
        binding.windSpeedTxt.text = "${formatNumberAccordingToLocale(appliedSettings.convertToSpeedUnit(weather!!.wind.speed), requireContext())}${appliedSettings.getSpeedUnit().getLocalizedSymbol(requireContext())}"
        binding.sunsetTxt.text = formatUnixTime.invoke(weather!!.sys.sunset)
        binding.pressureTxt.text = "${formatNumberAccordingToLocale(appliedSettings.convertToPressureUnit(weather!!.main.pressure.toDouble()), requireContext())}${appliedSettings.getPressureUnit().getLocalizedSymbol(requireContext())}"
        binding.humidityTxt.text = "${formatNumberAccordingToLocale(weather!!.main.humidity, requireContext())}%"
        binding.sunriseTxt.text = formatUnixTime.invoke(weather!!.sys.sunrise)
        binding.cloudCoverageTxt.text = "${formatNumberAccordingToLocale(weather!!.clouds.all, requireContext())}%"
    }

    private fun formatNumberAccordingToLocale(number: Number, context: Context): String {
        val locale = context.resources.configuration.locales.get(0)
        val formatter = NumberFormat.getInstance(locale)
        return formatter.format(number)
    }
}