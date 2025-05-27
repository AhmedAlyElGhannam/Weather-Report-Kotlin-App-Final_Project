package com.example.weather_report.features.settings.view

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weather_report.MainActivity
import com.example.weather_report.R
import com.example.weather_report.databinding.FragmentSettingsBinding
import com.example.weather_report.utils.AppliedSystemSettings
import com.example.weather_report.utils.AvailableLanguages
import com.example.weather_report.utils.LocaleHelper
import com.example.weather_report.utils.LocationOptions
import com.example.weather_report.utils.NotificationsOptions
import com.example.weather_report.utils.UnitSystem
import com.example.weather_report.utils.Units

class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    private val appliedSettings by lazy { AppliedSystemSettings.getInstance(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSettings()
        setupListeners()
    }

    private fun initSettings() {
        // Language
        when (appliedSettings.getLanguage().lang_name) {
            AvailableLanguages.ARABIC.lang_name -> binding.rbArabic.isChecked = true
            AvailableLanguages.ENGLISH.lang_name -> binding.rbEnglish.isChecked = true
            AvailableLanguages.FRENCH.lang_name -> binding.rbFrench.isChecked = true
            AvailableLanguages.SPANISH.lang_name -> binding.rbSpanish.isChecked = true
        }

        // Unit System
        when (appliedSettings.getUnitSystem()) {
            UnitSystem.STANDARD -> binding.rbSi.isChecked = true
            UnitSystem.IMPERIAL -> binding.rbImperial.isChecked = true
            UnitSystem.CUSTOM -> {
                binding.rbCustom.isChecked = true
                setCustomUnitsEnabled(true)
                // Set custom units from saved settings
                updateCustomUnitSelections()
            }
            else -> {}
        }

        // Notifications
        binding.toggleNotifications.isChecked =
            appliedSettings.getSelectedNotificationOption() == NotificationsOptions.NOTIFICATIONS_ON

        // Location
        when (appliedSettings.getSelectedLocationOption().desc) {
            LocationOptions.GPS.desc -> binding.rbGps.isChecked = true
            LocationOptions.MAP.desc -> binding.rbMap.isChecked = true
        }
    }

    private fun setupListeners() {
        // Language selection
        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_arabic -> appliedSettings.setLanguage(AvailableLanguages.ARABIC)
                R.id.rb_english -> appliedSettings.setLanguage(AvailableLanguages.ENGLISH)
                R.id.rb_french -> appliedSettings.setLanguage(AvailableLanguages.FRENCH)
                R.id.rb_spanish -> appliedSettings.setLanguage(AvailableLanguages.SPANISH)
            }

            updateAppLanguage()
        }

        // Location selection
        binding.rgLocationOptions.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_gps -> appliedSettings.setSelectedLocationOption(LocationOptions.GPS)
                R.id.rb_map -> appliedSettings.setSelectedLocationOption(LocationOptions.MAP)
            }
        }

        // Notifications toggle
        binding.toggleNotifications.setOnCheckedChangeListener { _, isChecked ->
            val option = if (isChecked) NotificationsOptions.NOTIFICATIONS_ON else NotificationsOptions.NOTIFICATIONS_OFF
            appliedSettings.setSelectedNotificationOption(option)
        }

        // Unit System selection
        binding.rgUnitSys.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_si -> {
                    appliedSettings.setUnitSystem(UnitSystem.STANDARD)
                    setCustomUnitsEnabled(false)
                    setDefaultUnitsForSystem(UnitSystem.STANDARD)
                    (activity as? MainActivity)?.refreshDataWithCurrentSettings()
                }
                R.id.rb_imperial -> {
                    appliedSettings.setUnitSystem(UnitSystem.IMPERIAL)
                    setCustomUnitsEnabled(false)
                    setDefaultUnitsForSystem(UnitSystem.IMPERIAL)
                    (activity as? MainActivity)?.refreshDataWithCurrentSettings()
                }
                R.id.rb_custom -> {
                    appliedSettings.setUnitSystem(UnitSystem.CUSTOM)
                    setCustomUnitsEnabled(true)
                    (activity as? MainActivity)?.refreshDataWithCurrentSettings()
                }
            }
        }

        // Custom Units Listeners
        binding.rgTemperatureUnit.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_celsius -> appliedSettings.setTempUnit(Units.CELSIUS)
                R.id.rb_fahrenheit -> appliedSettings.setTempUnit(Units.FAHRENHEIT)
                R.id.rb_kelvin -> appliedSettings.setTempUnit(Units.KELVIN)
            }
        }

        binding.rgSpeedUnit.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_mps -> appliedSettings.setSpeedUnit(Units.METERS_PER_SECOND)
                R.id.rb_kmh -> appliedSettings.setSpeedUnit(Units.KILOMETERS_PER_HOUR)
                R.id.rb_mph -> appliedSettings.setSpeedUnit(Units.MILES_PER_HOUR)
                R.id.rb_fps -> appliedSettings.setSpeedUnit(Units.FEET_PER_SECOND)
            }
        }

        binding.rgPressureUnit.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_hpa -> appliedSettings.setPressureUnit(Units.HECTOPASCAL)
                R.id.rb_atm -> appliedSettings.setPressureUnit(Units.ATMOSPHERE)
                R.id.rb_bar -> appliedSettings.setPressureUnit(Units.BAR)
                R.id.rb_psi -> appliedSettings.setPressureUnit(Units.PSI)
            }
        }
    }

    private fun setCustomUnitsEnabled(enable: Boolean) {
        // Temperature Units
        binding.rbCelsius.isEnabled = enable
        binding.rbFahrenheit.isEnabled = enable
        binding.rbKelvin.isEnabled = enable
        // Speed Units
        binding.rbMps.isEnabled = enable
        binding.rbKmh.isEnabled = enable
        binding.rbMph.isEnabled = enable
        binding.rbFps.isEnabled = enable
        // Pressure Units
        binding.rbHpa.isEnabled = enable
        binding.rbAtm.isEnabled = enable
        binding.rbBar.isEnabled = enable
        binding.rbPsi.isEnabled = enable
    }

    private fun updateCustomUnitSelections() {
        // Set checked states from current settings
        when (appliedSettings.getTempUnit()) {
            Units.CELSIUS -> binding.rbCelsius.isChecked = true
            Units.FAHRENHEIT -> binding.rbFahrenheit.isChecked = true
            Units.KELVIN -> binding.rbKelvin.isChecked = true
            else -> {}
        }
        when (appliedSettings.getSpeedUnit()) {
            Units.METERS_PER_SECOND -> binding.rbMps.isChecked = true
            Units.KILOMETERS_PER_HOUR -> binding.rbKmh.isChecked = true
            Units.MILES_PER_HOUR -> binding.rbMph.isChecked = true
            Units.FEET_PER_SECOND -> binding.rbFps.isChecked = true
            else -> {}
        }
        when (appliedSettings.getPressureUnit()) {
            Units.HECTOPASCAL -> binding.rbHpa.isChecked = true
            Units.ATMOSPHERE -> binding.rbAtm.isChecked = true
            Units.BAR -> binding.rbBar.isChecked = true
            Units.PSI -> binding.rbPsi.isChecked = true
            else -> {}
        }
    }

    private fun setDefaultUnitsForSystem(unitSystem: UnitSystem) {
        when (unitSystem) {
            UnitSystem.STANDARD -> {
                appliedSettings.setTempUnit(Units.KELVIN)
                appliedSettings.setSpeedUnit(Units.METERS_PER_SECOND)
                appliedSettings.setPressureUnit(Units.HECTOPASCAL)

                binding.rbKelvin.isChecked = true
                binding.rbMps.isChecked = true
                binding.rbHpa.isChecked = true
            }
            UnitSystem.IMPERIAL -> {
                appliedSettings.setTempUnit(Units.FAHRENHEIT)
                appliedSettings.setSpeedUnit(Units.MILES_PER_HOUR)
                appliedSettings.setPressureUnit(Units.HECTOPASCAL)

                binding.rbFahrenheit.isChecked = true
                binding.rbMph.isChecked = true
                binding.rbHpa.isChecked = true
            }
            else -> {}
        }
    }

    private fun updateAppLanguage() {
        val languageCode = appliedSettings.getLanguage().code
        LocaleHelper.applyLanguage(requireContext(), languageCode)
        activity?.window?.decorView?.layoutDirection =
            if (languageCode == "ar") View.LAYOUT_DIRECTION_RTL
            else View.LAYOUT_DIRECTION_LTR
        activity?.recreate()
    }
}