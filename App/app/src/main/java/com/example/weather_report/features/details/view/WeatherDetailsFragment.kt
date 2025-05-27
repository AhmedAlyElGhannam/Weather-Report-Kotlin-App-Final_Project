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
import com.example.weather_report.databinding.FragmentHomeScreenBinding
import com.example.weather_report.features.details.viewmodel.WeatherDetailsViewModel
import com.example.weather_report.features.home.view.DailyWeatherForecastAdapter
import com.example.weather_report.features.home.view.HourlyWeatherAdapter
import com.example.weather_report.model.pojo.WeatherResponse
import com.example.weather_report.utils.AppliedSystemSettings
import com.example.weather_report.utils.UnitSystem
import com.example.weather_report.utils.UnitSystemsConversions
import com.example.weather_report.utils.Units
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class WeatherDetailsFragment : Fragment() {
    lateinit var binding: FragmentHomeScreenBinding
    lateinit var hourlyWeatherAdapter: HourlyWeatherAdapter
    lateinit var dailyWeatherForecastAdapter: DailyWeatherForecastAdapter
    private var weather: WeatherResponse? = null
    private val weatherDetailsViewModel: WeatherDetailsViewModel by activityViewModels()

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
        weatherDetailsViewModel.weatherResponse
            .observe(viewLifecycleOwner) {
                weather = weatherDetailsViewModel.weatherResponse.value
                weather?.let {
                    applyUnits()
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

        binding.weatherConditionTxt.text = weather!!.weather[0].main

        binding.locationTxt.text = weather!!.name

        binding.highLowTempTxt.text = "↑ ${weather!!.main.temp_max}°${appliedSettings.getTempUnit().symbol} ↓ ${weather!!.main.temp_min}°${appliedSettings.getTempUnit().symbol}"

        binding.tempTxt.text = "${weather!!.main.temp}°${appliedSettings.getTempUnit().symbol}"

        binding.feelslikeTempTxt.text = "Feels Like ${weather!!.main.feels_like}°${appliedSettings.getTempUnit().symbol}"
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

    private fun applyUnits() {
        if (weather != null) {
            when (appliedSettings.getUnitSystem()) {
                UnitSystem.CUSTOM -> {
                    weather!!.main.temp = convertTemperature(weather!!.main.temp)

                    weather!!.main.temp_kf = convertTemperature(weather!!.main.temp_kf)

                    weather!!.main.feels_like = convertTemperature(weather!!.main.feels_like)

                    weather!!.main.temp_min = convertTemperature(weather!!.main.temp_min)

                    weather!!.main.temp_max = convertTemperature(weather!!.main.temp_max)

                    weather!!.wind.speed = when(appliedSettings.getSpeedUnit().symbol) {
                        Units.KILOMETERS_PER_HOUR.symbol -> UnitSystemsConversions.meterPerSecondToKilometerPerHour(weather!!.wind.speed)
                        Units.MILES_PER_HOUR.symbol -> UnitSystemsConversions.meterPerSecondToMilePerHour(weather!!.wind.speed)
                        Units.FEET_PER_SECOND.symbol -> UnitSystemsConversions.meterPerSecondToFeetPerSecond(weather!!.wind.speed)
                        else -> weather!!.wind.speed
                    }

                    weather!!.main.pressure = when(appliedSettings.getPressureUnit().symbol) {
                        Units.ATMOSPHERE.symbol -> UnitSystemsConversions.hectopascalToAtm(weather!!.main.pressure.toDouble()).toInt()
                        Units.BAR.symbol -> UnitSystemsConversions.hectopascalToBar(weather!!.main.pressure.toDouble()).toInt()
                        Units.PSI.symbol -> UnitSystemsConversions.hectopascalToPsi(weather!!.main.pressure.toDouble()).toInt()
                        else -> weather!!.main.pressure
                    }
                }
                UnitSystem.IMPERIAL -> {} // should be applied automatically
                UnitSystem.STANDARD -> {} // should be applied automatically
            }
        }
    }

    private fun convertTemperature(tempCelsius: Double): Double {
        return when (appliedSettings.getTempUnit()) {
            Units.FAHRENHEIT -> UnitSystemsConversions.celsiusToFahrenheit(tempCelsius)
            Units.KELVIN -> UnitSystemsConversions.celsiusToKelvin(tempCelsius)
            else -> tempCelsius
        }
    }
}