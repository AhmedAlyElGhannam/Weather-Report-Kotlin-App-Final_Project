package com.example.weather_report.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

interface LocationResultListener {
    fun onLocationSuccess(latitude: Double, longitude: Double, address: String)
    fun onLocationError(errorMessage: String)
}

class LocationHelper(
    private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val coroutineScope: CoroutineScope,
    private val listener: LocationResultListener
) {
    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun getFreshLocation() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0).build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    fusedLocationProviderClient.removeLocationUpdates(this)
                    val location = locationResult.lastLocation
                    if (location != null) {
                        coroutineScope.launch {
                            try {
                                val address = withContext(Dispatchers.IO) {
                                    val geocoder = Geocoder(context, Locale.getDefault())
                                    geocoder.getFromLocation(
                                        location.latitude,
                                        location.longitude,
                                        1
                                    )?.firstOrNull()?.getAddressLine(0) ?: "Address not found"
                                }
                                listener.onLocationSuccess(location.latitude, location.longitude, address)
                            } catch (e: Exception) {
                                listener.onLocationError("Error getting address: ${e.message}")
                            }
                        }
                    } else {
                        listener.onLocationError("Location not available")
                    }
                }
            },
            Looper.getMainLooper()
        )
    }
}
