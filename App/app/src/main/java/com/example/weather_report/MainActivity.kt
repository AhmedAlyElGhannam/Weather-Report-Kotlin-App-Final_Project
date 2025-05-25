package com.example.weather_report

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.weather_report.features.initialdialog.view.InitialSetupDialog
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Arrays
import org.osmdroid.views.overlay.Marker
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.weather_report.databinding.MainScreenBinding
import com.example.weather_report.features.mapdialog.view.MapDialog
import com.example.weather_report.model.pojo.Coordinates
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


class MainActivity : AppCompatActivity(), InitialChoiceCallback, ISelectedCoordinatesOnMapCallback {

    lateinit var bindingMainScreen : MainScreenBinding

    private lateinit var navController: NavController

    private val mainActivityViewModel : MainActivityViewModel by viewModels() {
        MainActivityViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                CityLocalDataSourceImpl(LocalDB.getInstance(this@MainActivity).getCityDao()),
                ForecastItemLocalDataSourceImpl(LocalDB.getInstance(this@MainActivity).getForecastItemDao()),
                CurrentWeatherLocalDataSourceImpl(LocalDB.getInstance(this@MainActivity).getCurrentWeatherDao()),
                WeatherAndForecastRemoteDataSourceImpl(
                    RetrofitHelper.retrofit.create(
                        IWeatherService::class.java))
            )
        )
    }

    private val LOCATION_PERMISSION_REQUESTCODE : Int = 1006
    private val LOCATION_PERMISSIONS : Array<String> = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        init {
            System.loadLibrary("weather_report")
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivityViewModel

        /*************************************************************************************************/

        bindingMainScreen = MainScreenBinding.inflate(layoutInflater)
        setContentView(bindingMainScreen.root)

        bindingMainScreen.fragmentContainer.isActivated = false

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController


        InitialSetupDialog(this@MainActivity).show(supportFragmentManager, "InitialSetupDialog")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUESTCODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getFreshLocation()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissions() : Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        return true
    }

    private fun enableLocationServices() {
        Toast.makeText(this , "Please Turn on Location" , Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun isLocationEnable() : Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun initializeNavigationDrawer() {
        // set up Toolbar
        setSupportActionBar(bindingMainScreen.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        // initialize DrawerToggle
        val toggle = ActionBarDrawerToggle(
            this@MainActivity,
            bindingMainScreen.drawerLayout,
            bindingMainScreen.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        bindingMainScreen.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        bindingMainScreen.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    if (navController.currentDestination?.id != R.id.homeFragment) {
                        navController.navigate(R.id.homeFragment)
                    }
                    bindingMainScreen.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_settings -> {
                    if (navController.currentDestination?.id != R.id.settingsFragment) {
                        navController.navigate(R.id.settingsFragment)
                    }
                    bindingMainScreen.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                // Add other menu items here
                else -> false
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getFreshLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                LOCATION_PERMISSIONS,
                LOCATION_PERMISSION_REQUESTCODE
            )
            return
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
            setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        }.build(),
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    val location = locationResult.lastLocation

                    if (location != null) {
                        Log.i("TAG", "onLocationResult: ${location.latitude} && ${location.longitude}")

                        mainActivityViewModel.fetchWeatherData(
                            location.latitude,
                            location.longitude
                        )

                        mainActivityViewModel.fetchForecastData(
                            location.latitude,
                            location.longitude
                        )

                        Log.i("TAG", "Location Secured. Coordinates: ${location.latitude}lat, ${location.longitude}")

                        // stop location updates
                        fusedLocationProviderClient.removeLocationUpdates(this)
                    }
                }}, Looper.myLooper())
    }

    override fun onGpsChosen() {
        if (checkPermissions()) {
            if (isLocationEnable()) {
                getFreshLocation()
            }
            else {
                enableLocationServices()
            }
        }
        else {
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS,LOCATION_PERMISSION_REQUESTCODE)
        }

        bindingMainScreen.fragmentContainer.isActivated = true
        initializeNavigationDrawer()
    }

    override fun onMapChosen() {
        MapDialog(this@MainActivity).show(supportFragmentManager, "MapDialog")
    }

    override fun onNotificationsEnabled() {
        // will come back to that
        Toast.makeText(this@MainActivity, "Notifications enabled", Toast.LENGTH_SHORT).show()
    }

    override fun onCoordinatesSelected(lat: Double, lon: Double) {
        mainActivityViewModel.fetchWeatherData(
            lat,
            lon
        )

        mainActivityViewModel.fetchForecastData(
            lat,
            lon
        )

        bindingMainScreen.fragmentContainer.isActivated = true
        initializeNavigationDrawer()
    }
}