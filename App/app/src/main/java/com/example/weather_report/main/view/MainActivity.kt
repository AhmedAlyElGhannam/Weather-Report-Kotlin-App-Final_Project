package com.example.weather_report.main.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.weather_report.features.initialdialog.view.InitialSetupDialog

import com.example.weather_report.model.local.LocalDB
import com.example.weather_report.model.remote.IWeatherService
import com.example.weather_report.model.remote.RetrofitHelper
import com.example.weather_report.model.remote.WeatherAndForecastRemoteDataSourceImpl
import com.example.weather_report.model.repository.WeatherRepositoryImpl
import kotlinx.coroutines.DelicateCoroutinesApi
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.weather_report.main.viewmodel.MainActivityViewModel
import com.example.weather_report.main.viewmodel.MainActivityViewModelFactory
import com.example.weather_report.R
import com.example.weather_report.databinding.MainScreenBinding
import com.example.weather_report.features.mapdialog.view.MapDialog

import com.example.weather_report.model.local.LocalDataSourceImpl
import com.example.weather_report.utils.AppliedSystemSettings
import com.example.weather_report.utils.GPSUtil
import com.example.weather_report.utils.ISelectedCoordinatesOnMapCallback
import com.example.weather_report.utils.InitialChoiceCallback
import com.example.weather_report.utils.LocaleHelper
import com.google.android.gms.location.FusedLocationProviderClient


class MainActivity : AppCompatActivity(),
    InitialChoiceCallback,
    ISelectedCoordinatesOnMapCallback {

    lateinit var bindingMainScreen : MainScreenBinding

    private lateinit var navController: NavController

    private var isConnected: Boolean = false

    private val gpsUtils by lazy { GPSUtil(this) }

    private val appliedSettings by lazy { AppliedSystemSettings.getInstance(this@MainActivity) }

    private val mainActivityViewModel : MainActivityViewModel by viewModels() {
        MainActivityViewModelFactory(
            WeatherRepositoryImpl.getInstance(WeatherAndForecastRemoteDataSourceImpl(
                    RetrofitHelper.retrofit.create(IWeatherService::class.java))
            ,LocalDataSourceImpl(LocalDB.getInstance(this@MainActivity).weatherDao())
        ))
    }

    private val LOCATION_PERMISSION_REQUESTCODE : Int = 1006
    private val NOTIFICATION_REQUESTCODE: Int = 9642
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


        /*************************************************************************************************/

        // network check
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)
        }
        isConnected = networkCapabilities?.let {
            it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } ?: false

        /*************************************************************************************************/

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController


        /*************************************************************************************************/

        if (isConnected) {
            // is initial setup or not
            if (appliedSettings.getIsInitialSetup()) {
                appliedSettings.setIsInitialSetup()
                // show initial setup dialog
                InitialSetupDialog(this@MainActivity).show(supportFragmentManager, "InitialSetupDialog")
            }
            else {
                // proceed with rest of logic
                mainActivityViewModel.loadLocalWeatherData()
                mainActivityViewModel.loadLocalForecastData()
            }
        }
        else {
            // is initial setup or not
            if (appliedSettings.getIsInitialSetup()) {
                appliedSettings.setIsInitialSetup()
                // show initial setup dialog
                Toast.makeText(this@MainActivity, "Please reconnect to the internet and restart the app", Toast.LENGTH_LONG).show()
                finish()
            }
            else {
                // fetch data from db
                mainActivityViewModel.loadLocalWeatherData()
                mainActivityViewModel.loadLocalForecastData()
            }
        }

        bindingMainScreen.fragmentContainer.isActivated = true
        initializeNavigationDrawer()

    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_PERMISSION_REQUESTCODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getFreshLocation()
//            } else {
//                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    override fun onGpsChosen() {
        if (gpsUtils.checkPermissions()) {
            if (gpsUtils.isLocationEnabled()) {
                getFreshLocation()
            } else {
                gpsUtils.enableLocationServices()
            }
        } else {
            gpsUtils.requestPermissions(this)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getFreshLocation() {
        gpsUtils.getCurrentLocation(object : GPSUtil.GPSLocationCallback {
            override fun onLocationResult(latitude: Double, longitude: Double) {
                onCoordinatesSelected(latitude, longitude)
            }

            override fun onLocationError(errorMessage: String) {
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        gpsUtils.handlePermissionResult(
            requestCode,
            grantResults,
            onPermissionGranted = { getFreshLocation() },
            onPermissionDenied = { Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show() }
        )
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
                        bindingMainScreen.toolbar.title = "Home"
                        navController.navigate(R.id.homeFragment)
                    }
                    bindingMainScreen.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_settings -> {
                    if (navController.currentDestination?.id != R.id.settingsFragment) {
                        bindingMainScreen.toolbar.title = "Settings"
                        navController.navigate(R.id.settingsFragment)
                    }
                    bindingMainScreen.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_alert -> {
                    if (navController.currentDestination?.id != R.id.alarmFragment) {
                        bindingMainScreen.toolbar.title = "Alerts"
                        navController.navigate(R.id.alarmFragment)
                    }
                    bindingMainScreen.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_fav -> {
                    if (navController.currentDestination?.id != R.id.favouriteLocationsFragment) {
                        bindingMainScreen.toolbar.title = "Favourite Locations"
                        navController.navigate(R.id.favouriteLocationsFragment)
                    }
                    bindingMainScreen.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                // Add other menu items here
                else -> false
            }
        }
    }

//    @OptIn(DelicateCoroutinesApi::class)
//    fun getFreshLocation() {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                LOCATION_PERMISSIONS,
//                LOCATION_PERMISSION_REQUESTCODE
//            )
//            return
//        }
//
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//
//        fusedLocationProviderClient.requestLocationUpdates(
//            LocationRequest.Builder(0).apply {
//            setPriority(Priority.PRIORITY_HIGH_ACCURACY)
//        }.build(),
//            object : LocationCallback() {
//                override fun onLocationResult(locationResult: LocationResult) {
//                    super.onLocationResult(locationResult)
//                    if (locationResult.lastLocation != null) {
//                        onCoordinatesSelected(
//                            locationResult.lastLocation!!.latitude,
//                            locationResult.lastLocation!!.longitude
//                        )
//                        // stop location updates
//                        fusedLocationProviderClient.removeLocationUpdates(this)
//                    }
//                }}, Looper.myLooper())
//    }
//
//    override fun onGpsChosen() {
//        if (checkPermissions()) {
//            if (isLocationEnable()) {
//                getFreshLocation()
//            }
//            else {
//                enableLocationServices()
//            }
//        }
//        else {
//            ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS,LOCATION_PERMISSION_REQUESTCODE)
//        }
//
//    }

    override fun onMapChosen() {
        MapDialog(this@MainActivity).show(supportFragmentManager, "MapDialog")
    }

    override fun onNotificationsEnabled() {
        // will come back to that
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, NOTIFICATION_REQUESTCODE)
        }

        if (!Settings.canDrawOverlays(this)) {
            val overlayIntent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${this.packageName}")
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            this.startActivity(overlayIntent)
            return
        }

        Toast.makeText(this@MainActivity, "Notifications enabled", Toast.LENGTH_SHORT).show()
    }

    override fun onCoordinatesSelected(lat: Double, lon: Double) {
        mainActivityViewModel.fetchWeatherData(
            isConnected,
            lat,
            lon
        )

        mainActivityViewModel.fetchForecastData(
            isConnected,
            lat,
            lon
        )
    }

    override fun attachBaseContext(newBase: Context) {
        val appliedSettings = AppliedSystemSettings.getInstance(newBase)
        super.attachBaseContext(
            LocaleHelper.applyLanguage(newBase, appliedSettings.getLanguage().code)
        )
    }

    private fun isDataFresh(lastUpdated: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val twentyFourHoursInMillis = 24 * 60 * 60 * 1000
        return (currentTime - lastUpdated) < twentyFourHoursInMillis
    }

    fun refreshDataWithCurrentSettings() {

    }
}