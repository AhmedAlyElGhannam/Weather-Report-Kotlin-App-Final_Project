package com.example.weather_report.contracts

interface SettingsScreenContract {
    interface View {
        fun updateAppLanguage()
        fun setupListeners()
        fun initSettings()
    }
}