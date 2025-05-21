package com.example.weather_report.utils

// note that SI is the standard---data is fetched remotely and stored locally in SI
class UnitSystemsConversions {
    companion object {
        // temperature conversions from celcius to other units
        public external fun celciusToKelvin(temp : Double) : Double
        public external fun celciusToFahrenheit(temp : Double) : Double

        // wind speed conversions from m/s to other units
        public external fun meterPerSecondToKilometerPerHour(windSpeed : Double) : Double
        public external fun meterPerSecondToMilePerHour(windSpeed : Double) : Double
        public external fun meterPerSecondToFeetPerSecond(windSpeed : Double) : Double

        // pressure conversions from hPa to other units
        public external fun hectopascalToPsi(pressure : Double) : Double
        public external fun hectopascalToAtm(pressure : Double) : Double
        public external fun hectopascalToBar(pressure : Double) : Double
    }
}