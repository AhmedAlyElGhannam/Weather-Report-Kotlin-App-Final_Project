package com.example.weather_report

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.weather_report.databinding.ActivityMainBinding
import com.example.weather_report.databinding.HomeScreenBinding
import com.example.weather_report.features.initialdialog.InitialSetupDialog
import com.example.weather_report.model.local.CityLocalDataSourceImpl
import com.example.weather_report.model.local.CurrentWeatherLocalDataSourceImpl
import com.example.weather_report.model.local.ForecastItemLocalDataSourceImpl
import com.example.weather_report.model.local.LocalDB
import com.example.weather_report.model.remote.IWeatherService
import com.example.weather_report.model.remote.RetrofitHelper
import com.example.weather_report.model.remote.WeatherAndForecastRemoteDataSourceImpl
import com.example.weather_report.model.repository.WeatherRepositoryImpl
import com.example.weather_report.utils.UnitSystem
import com.example.weather_report.utils.UnitSystemsConversions
import com.example.weather_report.utils.WeatherResponseToWeatherLocalDataSourceMapper.toCurrentWeather
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import java.util.Arrays
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker


class MainActivity : AppCompatActivity() {

    lateinit var binderHome : HomeScreenBinding
    lateinit var binderMainActivity: ActivityMainBinding

    companion object {
        init {
            System.loadLibrary("weather_report")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*************************************************************************************************/

        binderMainActivity = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binderMainActivity.root)

        /*************************************************************************************************/

        val repo : WeatherRepositoryImpl = WeatherRepositoryImpl.getInstance(
            CityLocalDataSourceImpl(LocalDB.getInstance(this@MainActivity).getCityDao()),
            ForecastItemLocalDataSourceImpl(LocalDB.getInstance(this@MainActivity).getForecastItemDao()),
            CurrentWeatherLocalDataSourceImpl(LocalDB.getInstance(this@MainActivity).getCurrentWeatherDao()),
            WeatherAndForecastRemoteDataSourceImpl(RetrofitHelper.retrofit.create(IWeatherService::class.java))
        )

        /*************************************************************************************************/

        val dialog = InitialSetupDialog()
        dialog.show(supportFragmentManager, "InitialSetupDialog")

        /*************************************************************************************************/

        var currentMarker: Marker? = null

        // Set user agent
        Configuration.getInstance().userAgentValue = packageName
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))

        val map = binderMainActivity.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)

        map.setMinZoomLevel(4.0)
        map.setMaxZoomLevel(18.0)

        map.controller.setZoom(4.0)

        // Create the map event receiver
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                // Remove old marker if it exists
                currentMarker?.let {
                    map.overlays.remove(it)
                }

                // Create a new marker
                val marker = Marker(map)
                marker.position = p
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = "Pinned Location"
                marker.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_map_pin_red)

                // Add the new marker
                map.overlays.add(marker)
                currentMarker = marker // Track it
                map.invalidate()

                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }

        // Add the event overlay only once
        val overlayEvents = MapEventsOverlay(mapEventsReceiver)
        map.overlays.add(overlayEvents)

        /*************************************************************************************************/

        GlobalScope.launch(Dispatchers.IO) {
            val res_forecast = repo.fetchForecastDataRemotely(
                lat = 39.0444,
                lon = 69.2357,
                units = UnitSystem.METRIC.value
            )

            val res_currWeather = repo.fetchCurrentWeatherDataRemotely(
                lat = 39.0444,
                lon = 69.2357,
                units = UnitSystem.METRIC.value
            )

            res_forecast?.city?.let { repo.addCityToFavourites(it) }

            if (res_currWeather != null) {
                repo.insertCurrentWeather(res_currWeather.toCurrentWeather())
                if (res_forecast != null) {
                    repo.saveLocationForecastData(res_forecast.list.map { it.copy(cityId = res_forecast.city.id) })
                    val dum_list = repo.getForecastItemsByCityID(res_forecast.city.id)
                    Log.i("TAG", "onCreate: " + dum_list.toString())
                }

            }

            withContext(Dispatchers.Main) {
                Log.i("TAG", "forecast: " + res_forecast.toString())
                Log.i("TAG", "currWeather: " + res_currWeather.toString())
            }
        }

        /*************************************************************************************************/

        // testing temp conversion
        Log.i("TAG",
            "40C = ${UnitSystemsConversions.celsiusToKelvin(40.0)}K = ${UnitSystemsConversions.celsiusToFahrenheit(40.0)}F"
        )
        // testing wind speed conversion
        Log.i("TAG",
            "10m/s = ${UnitSystemsConversions.meterPerSecondToKilometerPerHour(10.0)}km/h = ${UnitSystemsConversions.meterPerSecondToMilePerHour(10.0)}mph = ${UnitSystemsConversions.meterPerSecondToFeetPerSecond(10.0)}ft/s"
        )
        // testing pressure conversions
        Log.i("TAG", "10000hpa = ${UnitSystemsConversions.hectopascalToAtm(10000.0)}atm = ${UnitSystemsConversions.hectopascalToPsi(10000.0)}psi = ${UnitSystemsConversions.hectopascalToBar(10000.0)}bar")
        // for showing phone cpu arch
        Log.i("TAG", "CPU Arch = ${Arrays.toString(Build.SUPPORTED_ABIS)}")

        /*************************************************************************************************/


    }
}