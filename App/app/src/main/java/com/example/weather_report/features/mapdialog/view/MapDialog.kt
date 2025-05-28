package com.example.weather_report.features.mapdialog.view

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import androidx.core.content.ContextCompat
import com.example.weather_report.utils.callback.ISelectedCoordinatesOnMapCallback
import com.example.weather_report.R
import com.example.weather_report.databinding.FragmentMapBinding
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.net.URLEncoder

class MapDialog(private val listener: ISelectedCoordinatesOnMapCallback) : DialogFragment() {

    lateinit var binding : FragmentMapBinding
    private var currentMarker: Marker? = null
    private val suggestions = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogTheme)
        isCancelable = false

        // initializing osmdroid configuration
        Configuration.getInstance().userAgentValue = requireContext().packageName
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // map initialization
        val map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)
        map.minZoomLevel = 4.0
        map.maxZoomLevel = 15.0
        map.controller.setZoom(4.0)
        map.visibility = View.VISIBLE
        Log.d("TAG", "MapView initialized with size: ${map.width}x${map.height}")

        // tap event
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                updateMarker(p, "Pinned Location")
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }
        map.overlays.add(MapEventsOverlay(mapEventsReceiver))

        // AutoCompleteTextView shenanigans
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions)
        binding.searchBar.setAdapter(adapter)
        binding.searchBar.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = suggestions[position]
            searchCity(selectedCity)
        }

        // handling enter key press (to do a search by name)
        binding.searchBar.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val query = binding.searchBar.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchCity(query)
                    binding.searchBar.dismissDropDown()
                }
                true
            } else {
                false
            }
        }

        // search input with debouncing shenanigans
        binding.searchBar.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                searchJob?.cancel()
                val query = s.toString().trim()
                if (query.length >= 2) {
                    searchJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(325) // debounce for 300ms to avoid headaches
                        fetchCitySuggestions(query)
                    }
                }
            }
        })

        // proceed button handler
        binding.btnProceed.setOnClickListener {
            if (currentMarker != null) {
                listener.onCoordinatesSelected(
                    currentMarker!!.position.latitude,
                    currentMarker!!.position.longitude
                )
                Log.i("TAG", "Coordinates: ${currentMarker!!.position.latitude}, ${currentMarker!!.position.longitude}")
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Please mark a place or select a city to continue", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMarker(geoPoint: GeoPoint, title: String) {
        val map = binding.map
        if (currentMarker == null) {
            currentMarker = Marker(map).apply {
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_map_pin_red)
                map.overlays.add(this)
            }
        }
        currentMarker?.position = geoPoint
        currentMarker?.title = title
        map.controller.animateTo(geoPoint)
        map.invalidate()
        Log.d("TAG", "Marker updated at: $geoPoint")
    }

    private fun fetchCitySuggestions(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val url = "https://nominatim.openstreetmap.org/search?q=${URLEncoder.encode(query, "UTF-8")}&format=json&addressdetails=1&limit=5"
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", requireContext().packageName)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Search failed: ${response.message}", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    val json = response.body?.string() ?: return@launch
                    val jsonArray = JSONArray(json)
                    val newSuggestions = mutableListOf<String>()
                    for (i in 0 until jsonArray.length()) {
                        val result = jsonArray.getJSONObject(i)
                        newSuggestions.add(result.getString("display_name"))
                    }

                    withContext(Dispatchers.Main) {
                        suggestions.clear()
                        suggestions.addAll(newSuggestions)
                        adapter.notifyDataSetChanged()
                        Log.d("TAG", "Suggestions updated: $suggestions")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Search failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun searchCity(cityName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val url = "https://nominatim.openstreetmap.org/search?q=${URLEncoder.encode(cityName, "UTF-8")}&format=json&limit=1"
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", requireContext().packageName)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Search failed: ${response.message}", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    val json = response.body?.string() ?: return@launch
                    val jsonArray = JSONArray(json)
                    if (jsonArray.length() == 0) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    val result = jsonArray.getJSONObject(0)
                    val lat = result.getDouble("lat")
                    val lon = result.getDouble("lon")
                    val displayName = result.getString("display_name")

                    withContext(Dispatchers.Main) {
                        val geoPoint = GeoPoint(lat, lon)
                        updateMarker(geoPoint, displayName)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Search failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}