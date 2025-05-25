package com.example.weather_report.utils

class AppliedSystemSettings {
    companion object {
        @Volatile
        private var _unitSystem = UnitSystem.METRIC.value

        @Volatile
        private var _speedUnit = Units.METERS_PER_SECOND.symbol

        @Volatile
        private var _tempUnit = Units.CELSIUS.symbol

        @Volatile
        private var _pressureUnit = Units.HECTOPASCAL.symbol

        @Volatile
        private var _distanceUnit = Units.METER.symbol

        @Synchronized
        fun getUnitSystem(): String = _unitSystem

        @Synchronized
        fun setUnitSystem(value: String) {
            _unitSystem = value
        }

        @Synchronized
        fun getSpeedUnit(): String = _speedUnit

        @Synchronized
        fun setSpeedUnit(value: String) {
            _speedUnit = value
        }

        @Synchronized
        fun getTempUnit(): String = _tempUnit

        @Synchronized
        fun setTempUnit(value: String) {
            _tempUnit = value
        }

        @Synchronized
        fun getPressureUnit(): String = _pressureUnit

        @Synchronized
        fun setPressureUnit(value: String) {
            _pressureUnit = value
        }

        @Synchronized
        fun getDistanceUnit(): String = _distanceUnit

        @Synchronized
        fun setDistanceUnit(value: String) {
            _distanceUnit = value
        }
    }
}
