package com.example.weather_report.features.mapdialog.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.weather_report.utils.ISelectedCoordinatesOnMapCallback
import com.example.weather_report.R
import com.example.weather_report.databinding.FragmentMapBinding
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class MapDialog(private val listener : ISelectedCoordinatesOnMapCallback) : DialogFragment() {

    lateinit var binding: FragmentMapBinding
    lateinit var currentMarker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        binding = FragmentMapBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)

        // set user agent
        Configuration.getInstance().userAgentValue = requireContext().packageName
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))

        val map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)

        map.minZoomLevel = 4.0
        map.maxZoomLevel = 18.0

        map.controller.setZoom(4.0)

        // create the map event receiver
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                if (!::currentMarker.isInitialized) {
                    // First time: create the marker
                    currentMarker = Marker(map).apply {
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Pinned Location"
                        icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_map_pin_red)
                        map.overlays.add(this)
                    }
                }

                // update the marker position
                currentMarker.position = p
                map.invalidate()

                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }

        val overlayEvents = MapEventsOverlay(mapEventsReceiver)
        map.overlays.add(overlayEvents)

        binding.btnProceed.setOnClickListener {
            if (currentMarker != null) {
                listener.onCoordinatesSelected(
                    currentMarker.position.latitude,
                    currentMarker.position.longitude
                )
                Log.i("TAG", "From Map DialogFragment: ${currentMarker.position.latitude} && ${currentMarker.position.longitude}" )
                dismiss()
            }
            else {
                Toast.makeText(requireContext(), "Please mark a place on the map to continue", Toast.LENGTH_SHORT).show()
            }
        }

        return builder.create()
    }
}

