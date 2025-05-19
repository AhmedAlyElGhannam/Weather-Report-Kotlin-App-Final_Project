package com.example.weather_report

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weather_report.databinding.ActivityMainBinding
import com.example.weather_report.databinding.HomeScreenBinding

class MainActivity : AppCompatActivity() {

    lateinit var binder : HomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen)


    }
}