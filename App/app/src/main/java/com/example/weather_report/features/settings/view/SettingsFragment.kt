package com.example.weather_report.features.settings.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weather_report.R
import com.example.weather_report.databinding.FragmentHomeScreenBinding
import com.example.weather_report.databinding.FragmentSettingsBinding
import com.example.weather_report.databinding.MainScreenBinding
import com.example.weather_report.utils.AppliedSystemSettings
import com.example.weather_report.utils.AvailableLanguages
import com.example.weather_report.utils.LocationOptions
import com.example.weather_report.utils.NotificationsOptions
import com.example.weather_report.utils.UnitSystem

class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (AppliedSystemSettings.getLanguage().lang_name) {
            AvailableLanguages.ARABIC.lang_name -> binding.rbArabic.isChecked = true
            AvailableLanguages.ENGLISH.lang_name -> binding.rbEnglish.isChecked = true
            AvailableLanguages.FRENCH.lang_name -> binding.rbFrench.isChecked = true
            AvailableLanguages.SPANISH.lang_name -> binding.rbSpanish.isChecked = true

        }

        when (AppliedSystemSettings.getUnitSystem().value) {
            UnitSystem.STANDARD.value -> binding.rbSi.isChecked = true
            UnitSystem.IMPERIAL.value -> binding.rbImperial.isChecked = true
            UnitSystem.CUSTOM.value -> {
                binding.rbCustom.isChecked = true
                enableCustomUnitsSelections()
                binding.rbCelsius.isChecked = true
                binding.rbMps.isChecked = true
                binding.rbHpa.isChecked = true
            }
        }

        when (AppliedSystemSettings.getSelectedNotificationOption().desc) {
            NotificationsOptions.NOTIFICATIONS_ON.desc -> binding.toggleNotifications.isChecked = true
            NotificationsOptions.NOTIFICATIONS_OFF.desc -> binding.toggleNotifications.isChecked = false
        }

        when (AppliedSystemSettings.getSelectedLocationOption().desc) {
            LocationOptions.GPS.desc -> binding.rbGps.isChecked = true
            LocationOptions.MAP.desc -> binding.rbMap.isChecked = true
        }

    }

    private fun enableCustomUnitsSelections() {
        binding.rbFahrenheit.isActivated = true
        binding.rbHpa.isActivated = true
        binding.rbMps.isActivated = true
        binding.rbAtm.isActivated = true
        binding.rbFps.isActivated = true
        binding.rbKelvin.isActivated = true
        binding.rbKmh.isActivated = true
        binding.rbMph.isActivated = true
        binding.rbPsi.isActivated = true
    }
}