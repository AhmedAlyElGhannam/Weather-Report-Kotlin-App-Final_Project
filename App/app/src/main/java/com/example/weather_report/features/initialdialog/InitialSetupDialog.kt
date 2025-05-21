package com.example.weather_report.features.initialdialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.weather_report.R
import com.example.weather_report.databinding.DialogInitialSetupBinding

class InitialSetupDialog : DialogFragment() {

    lateinit var binding : DialogInitialSetupBinding

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        binding = DialogInitialSetupBinding.inflate(LayoutInflater.from(context))

        builder.setView(binding.root)

        binding.btnOk.setOnClickListener {
            val selectedOption = if (binding.radioGps.isChecked) {
                "GPS"
            }
            else if (binding.radioMap.isChecked) {
                "Map"
            }
            else {
                null
            }

            val notificationsEnabled = binding.toggleNotifications.isEnabled

            Toast.makeText(context, "Location: $selectedOption, Notifications: $notificationsEnabled", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        return builder.create()
    }
}
