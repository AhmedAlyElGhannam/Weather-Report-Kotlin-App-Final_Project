package com.example.weather_report.features.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
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
import com.example.weather_report.utils.callback.ISelectedCoordinatesOnMapCallback
import com.example.weather_report.main.viewmodel.MainActivityViewModel
import com.example.weather_report.R
import com.example.weather_report.contracts.HomeScreenContract
import com.example.weather_report.databinding.FragmentHomeScreenBinding
import com.example.weather_report.features.mapdialog.view.MapDialog
import com.example.weather_report.main.viewmodel.MainActivityViewModelFactory
import com.example.weather_report.model.local.LocalDB
import com.example.weather_report.model.local.LocalDataSourceImpl
import com.example.weather_report.model.pojo.response.WeatherResponse
import com.example.weather_report.model.remote.IWeatherService
import com.example.weather_report.model.remote.RetrofitHelper
import com.example.weather_report.model.remote.WeatherAndForecastRemoteDataSourceImpl
import com.example.weather_report.model.repository.WeatherRepositoryImpl
import com.example.weather_report.utils.settings.AppliedSystemSettings
import com.example.weather_report.utils.gps.GPSUtil
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeScreenFragment
    : Fragment(),
    HomeScreenContract.View,
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
    
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels() {
        MainActivityViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                WeatherAndForecastRemoteDataSourceImpl(
                    RetrofitHelper.retrofit.create(IWeatherService::class.java))
                , LocalDataSourceImpl(LocalDB.getInstance(requireContext()).weatherDao())
            ))
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
            if (isConnectedToInternet()) {
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
            else {
                Toast.makeText(requireContext(), "Please connect to internet to refresh", Toast.LENGTH_SHORT).show()
            }
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

        binding.weatherImg.playAnimation()

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

    override fun onCoordinatesSelected(lat: Double, lon: Double) {
        mainActivityViewModel.fetchWeatherData(
            isConnectedToInternet(),
            lat,
            lon
        )

        mainActivityViewModel.fetchForecastData(
            isConnectedToInternet(),
            lat,
            lon
        )
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

    private fun formatNumberAccordingToLocale(number: Number, context: Context): String {
        val locale = context.resources.configuration.locales.get(0)
        val formatter = NumberFormat.getInstance(locale)
        return formatter.format(number)
    }
}