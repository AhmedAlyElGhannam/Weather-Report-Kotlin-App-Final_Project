package com.example.weather_report.features.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_report.utils.ISelectedCoordinatesOnMapCallback
import com.example.weather_report.main.view.MainActivity
import com.example.weather_report.main.viewmodel.MainActivityViewModel
import com.example.weather_report.R
import com.example.weather_report.databinding.FragmentHomeScreenBinding
import com.example.weather_report.features.mapdialog.view.MapDialog
import com.example.weather_report.model.pojo.WeatherResponse
import com.example.weather_report.utils.AppliedSystemSettings
import com.example.weather_report.utils.GPSUtil
import com.example.weather_report.utils.UnitSystem
import com.example.weather_report.utils.UnitSystemsConversions
import com.example.weather_report.utils.Units
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeScreenFragment : Fragment(),
    ISelectedCoordinatesOnMapCallback {
    lateinit var binding: FragmentHomeScreenBinding
    lateinit var hourlyWeatherAdapter: HourlyWeatherAdapter
    lateinit var dailyWeatherForecastAdapter: DailyWeatherForecastAdapter
    private var hasFetchedWeatherData = false
    private var hasFetchedForecastData = false
    private var weather: WeatherResponse? = null
    private val appliedSettings by lazy { AppliedSystemSettings.getInstance(requireContext()) }
    private val gpsUtils by lazy { GPSUtil(requireContext()) }

    val formatUnixTime: (Long) -> String = { unixTime ->
        val date = Date(unixTime * 1000)
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        format.timeZone = TimeZone.getDefault()
        format.format(date)
    }
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

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

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (appliedSettings.getSelectedLocationOption().optName == "GPS") {
                gpsUtils.getCurrentLocation(object : GPSUtil.GPSLocationCallback {
                    override fun onLocationResult(latitude: Double, longitude: Double) {
                        onCoordinatesSelected(latitude, longitude)
                        binding.swipeRefreshLayout.isRefreshing = false
                    }

                    override fun onLocationError(errorMessage: String) {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                })
            } else {
                activity?.let { MapDialog(this).show(it.supportFragmentManager, "MapDialog") }
            }
            hasFetchedWeatherData = false
            hasFetchedForecastData = false
        }
    }

    private fun setupObservers() {
        mainActivityViewModel.weatherResponse
            .observe(viewLifecycleOwner) {
                if (!hasFetchedWeatherData) {
                weather = mainActivityViewModel.weatherResponse.value
                hasFetchedWeatherData = true

                weather?.let {
                    updateWeatherUI()
                    updateExtraInfo()
                    binding.swipeRefreshLayout.isRefreshing = false
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
    private fun updateWeatherUI() {
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

        binding.weatherImg.playAnimation()

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
    private fun updateExtraInfo() {
        binding.windSpeedTxt.text = "${weather!!.wind.speed}${appliedSettings.getSpeedUnit().symbol}"
        binding.sunsetTxt.text = formatUnixTime.invoke(weather!!.sys.sunset)
        binding.pressureTxt.text = "${weather!!.main.pressure}${appliedSettings.getPressureUnit().symbol}"
        binding.humidityTxt.text = "${weather!!.main.humidity}%"
        binding.sunriseTxt.text = formatUnixTime.invoke(weather!!.sys.sunrise)
        binding.cloudCoverageTxt.text = "${weather!!.clouds.all}%"
    }

    private fun convertTemperature(tempCelsius: Double): Double {
        return when (appliedSettings.getTempUnit()) {
            Units.FAHRENHEIT -> UnitSystemsConversions.celsiusToFahrenheit(tempCelsius)
            Units.KELVIN -> UnitSystemsConversions.celsiusToKelvin(tempCelsius)
            else -> tempCelsius
        }
    }

    override fun onCoordinatesSelected(lat: Double, lon: Double) {
        (activity as? MainActivity)?.onCoordinatesSelected(lat, lon)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        gpsUtils.handlePermissionResult(
            requestCode,
            grantResults,
            onPermissionGranted = {
                binding.swipeRefreshLayout.isRefreshing = true
                gpsUtils.getCurrentLocation(object : GPSUtil.GPSLocationCallback {
                    override fun onLocationResult(latitude: Double, longitude: Double) {
                        onCoordinatesSelected(latitude, longitude)
                        binding.swipeRefreshLayout.isRefreshing = false
                    }

                    override fun onLocationError(errorMessage: String) {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                })
            },
            onPermissionDenied = {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        )
    }
}