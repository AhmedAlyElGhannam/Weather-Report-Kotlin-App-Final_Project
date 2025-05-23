package com.example.weather_report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather_report.model.pojo.Coordinates

class MainActivityViewModel : ViewModel() {
    private val _coordinates = MutableLiveData<Coordinates>()
    val coordinates : LiveData<Coordinates> = _coordinates

    fun setCoordinates(_coord : Coordinates) {
        _coordinates.value = _coord
    }
}