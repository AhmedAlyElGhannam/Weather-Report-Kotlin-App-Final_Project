package com.example.weather_report.features.initialdialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.weather_report.R

class InitialSetupDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.DialogTheme)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_initial_setup, null)

        val radioGroup = view.findViewById<RadioGroup>(R.id.location_radio_group)
        val notificationSwitch = view.findViewById<Switch>(R.id.switch_notifications)
        val okButton = view.findViewById<Button>(R.id.btn_ok)

        builder.setView(view)

        okButton.setOnClickListener {
            val selectedOption = when (radioGroup.checkedRadioButtonId) {
                R.id.radio_gps -> "GPS"
                R.id.radio_map -> "Map"
                else -> "None"
            }
            val notificationsEnabled = notificationSwitch.isChecked

            Toast.makeText(context, "Location: $selectedOption, Notifications: $notificationsEnabled", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        return builder.create()
    }
}
