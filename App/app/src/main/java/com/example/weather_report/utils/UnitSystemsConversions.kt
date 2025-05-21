package com.example.weather_report.utils

// note that SI is the standard---data is fetched remotely and stored locally in SI
class UnitSystemsConversions {
    companion object {
        // temperature conversions from celcius to other units
        public external fun celciusToKelvin()
        public external fun celciusToFahrenheit()

        // wind speed conversions from m/s to other units
        public external fun meterPerSecondToKilometerPerHour()
        public external fun meterPerSecondToMilePerHour()
        public external fun meterPerSecondToKilometerPerHour()

        // pressure conversions from hPa to other units
        public external fun hectopascalToPsi()
        public external fun hectopascalToAtm()
        public external fun hectopascalToBar()
    }
}