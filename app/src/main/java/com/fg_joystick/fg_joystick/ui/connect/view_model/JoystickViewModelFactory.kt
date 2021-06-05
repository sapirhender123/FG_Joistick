package com.fg_joystick.fg_joystick.ui.connect.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fg_joystick.fg_joystick.data.Client

/**
 * ViewModel provider factory to instantiate connectViewModel.
 * Required given connectViewModel has a non-empty constructor
 */
class JoystickViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JoystickViewModel::class.java)) {
            return JoystickViewModel(
                client = Client
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}