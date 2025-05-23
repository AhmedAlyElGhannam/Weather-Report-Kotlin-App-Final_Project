package com.example.weather_report.features.initialdialog.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.weather_report.InitialChoiceCallback
import com.example.weather_report.databinding.DialogInitialSetupBinding

class InitialSetupDialog(private val listener: InitialChoiceCallback) : DialogFragment() {

    lateinit var binding: DialogInitialSetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        binding = DialogInitialSetupBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)

        binding.btnOk.setOnClickListener {
            when {
                binding.radioGps.isChecked -> {
                    listener.onGpsChosen()
                    if (binding.toggleNotifications.isChecked) listener.onNotificationsEnabled()
                    dismiss()
                }
                binding.radioMap.isChecked -> {
                    listener.onMapChosen()
                    if (binding.toggleNotifications.isChecked) listener.onNotificationsEnabled()
                    dismiss()
                }
                else -> {
                    Toast.makeText(context, "Please select either map or GPS to continue", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return builder.create()
    }
}

