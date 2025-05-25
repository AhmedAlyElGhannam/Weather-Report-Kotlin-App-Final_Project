package com.example.weather_report.utils

class AppliedSystemSettings {
    companion object {
        @Volatile
        private var _unitSystem = UnitSystem.METRIC

        @Volatile
        private var _speedUnit = Units.METERS_PER_SECOND

        @Volatile
        private var _tempUnit = Units.CELSIUS

        @Volatile
        private var _pressureUnit = Units.HECTOPASCAL

        @Volatile
        private var _distanceUnit = Units.METER

        @Volatile
        private var _language = AvailableLanguages.ENGLISH

        @Volatile
        private var _location = LocationOptions.GPS

        @Volatile
        private var _notifications = NotificationsOptions.NOTIFICATIONS_OFF

        @Synchronized
        fun getSelectedNotificationOption(): NotificationsOptions = _notifications

        @Synchronized
        fun setSelectedNotificationOption(_notif: NotificationsOptions) {
            _notifications = _notif
        }

        @Synchronized
        fun getSelectedLocationOption(): LocationOptions = _location

        @Synchronized
        fun setSelectedLocationOption(_loc: LocationOptions) {
            _location = _loc
        }

        @Synchronized
        fun getLanguage(): AvailableLanguages = _language

        @Synchronized
        fun setLanguage(_lang: AvailableLanguages) {
            _language = _lang
        }

        @Synchronized
        fun getUnitSystem(): UnitSystem = _unitSystem

        @Synchronized
        fun setUnitSystem(value: UnitSystem) {
            _unitSystem = value
        }

        @Synchronized
        fun getSpeedUnit(): Units = _speedUnit

        @Synchronized
        fun setSpeedUnit(value: Units) {
            _speedUnit = value
        }

        @Synchronized
        fun getTempUnit(): Units = _tempUnit

        @Synchronized
        fun setTempUnit(value: Units) {
            _tempUnit = value
        }

        @Synchronized
        fun getPressureUnit(): Units = _pressureUnit

        @Synchronized
        fun setPressureUnit(value: Units) {
            _pressureUnit = value
        }

        @Synchronized
        fun getDistanceUnit(): Units = _distanceUnit

        @Synchronized
        fun setDistanceUnit(value: Units) {
            _distanceUnit = value
        }
    }
}
