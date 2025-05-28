package com.example.weather_report.main.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weather_report.features.initialdialog.view.InitialSetupDialog

import com.example.weather_report.model.local.LocalDB
import com.example.weather_report.model.remote.IWeatherService
import com.example.weather_report.model.remote.RetrofitHelper
import com.example.weather_report.model.remote.WeatherAndForecastRemoteDataSourceImpl
import com.example.weather_report.model.repository.WeatherRepositoryImpl
import android.content.Context
import android.content.Intent
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
import com.example.weather_report.contracts.MainActivityContract
import com.example.weather_report.databinding.MainScreenBinding
import com.example.weather_report.features.mapdialog.view.MapDialog

import com.example.weather_report.model.local.LocalDataSourceImpl
import com.example.weather_report.utils.settings.AppliedSystemSettings
import com.example.weather_report.utils.gps.GPSUtil
import com.example.weather_report.utils.callback.ISelectedCoordinatesOnMapCallback
import com.example.weather_report.utils.callback.InitialChoiceCallback
import com.example.weather_report.utils.settings.lang.LocaleHelper
import com.example.weather_report.utils.WorkScheduler


class MainActivity : AppCompatActivity(),
    InitialChoiceCallback,
    ISelectedCoordinatesOnMapCallback,
    MainActivityContract.View {

    private lateinit var bindingMainScreen : MainScreenBinding
    private lateinit var navController: NavController
    private val gpsUtils by lazy { GPSUtil(this) }
    private val appliedSettings by lazy { AppliedSystemSettings.getInstance(this@MainActivity) }
    private val NOTIFICATION_REQUESTCODE: Int = 9642
    private val mainActivityViewModel : MainActivityViewModel by viewModels() {
        MainActivityViewModelFactory(
            WeatherRepositoryImpl.getInstance(WeatherAndForecastRemoteDataSourceImpl(
                    RetrofitHelper.retrofit.create(IWeatherService::class.java))
            ,LocalDataSourceImpl(LocalDB.getInstance(this@MainActivity).weatherDao())
        ))
    }

    companion object {
        init {
            // unit conversion jni lib init
            System.loadLibrary("weather_report")
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // hanging instance of viewmodel to solve an error
        mainActivityViewModel

        // start data fetcher worker
        WorkScheduler.scheduleDailyDataFetch(this@MainActivity)

        /*************************************************************************************************/

        // view binding shenanigans
        bindingMainScreen = MainScreenBinding.inflate(layoutInflater)
        setContentView(bindingMainScreen.root)

        bindingMainScreen.fragmentContainer.isActivated = false

        /*************************************************************************************************/

        // navigation controller shenanigans
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        /*************************************************************************************************/

        // initial setup
        if (isConnectedToInternet()) {
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

        /*************************************************************************************************/

        // initialize drawer
        bindingMainScreen.fragmentContainer.isActivated = true
        initializeNavigationDrawer()

    }

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

    override fun getFreshLocation() {
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

    override fun initializeNavigationDrawer() {
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
//                        bindingMainScreen.toolbar.title = "Home"
                        navController.navigate(R.id.homeFragment)
                    }
                    bindingMainScreen.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_settings -> {
                    if (navController.currentDestination?.id != R.id.settingsFragment) {
//                        bindingMainScreen.toolbar.title = "Settings"
                        navController.navigate(R.id.settingsFragment)
                    }
                    bindingMainScreen.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_alert -> {
                    if (navController.currentDestination?.id != R.id.alarmFragment) {
//                        bindingMainScreen.toolbar.title = "Alerts"
                        navController.navigate(R.id.alarmFragment)
                    }
                    bindingMainScreen.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_fav -> {
                    if (navController.currentDestination?.id != R.id.favouriteLocationsFragment) {
//                        bindingMainScreen.toolbar.title = "Favourite Locations"
                        navController.navigate(R.id.favouriteLocationsFragment)
                    }
                    bindingMainScreen.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }
    }

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

    override fun attachBaseContext(newBase: Context) {
        val appliedSettings = AppliedSystemSettings.getInstance(newBase)
        super.attachBaseContext(
            LocaleHelper.applyLanguage(newBase, appliedSettings.getLanguage().code)
        )
    }

    private fun isConnectedToInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)
        }
        return networkCapabilities?.let {
            it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } ?: false
    }
}