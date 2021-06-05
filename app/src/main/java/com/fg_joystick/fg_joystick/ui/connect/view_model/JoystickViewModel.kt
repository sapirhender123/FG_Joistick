package com.fg_joystick.fg_joystick.ui.connect.view_model

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.fg_joystick.fg_joystick.data.Client
import com.fg_joystick.fg_joystick.ui.connect.view.JoystickActivity

class JoystickViewModel(private val client: Client) : ViewModel() {
    fun setAileron(a: Float) {
        Log.d("aileron", a.toString())
        client.sendMessage("aileron", a)
    }

    fun setElevator(a: Float) {
        Log.d("elevator", a.toString())
        client.sendMessage("elevator", a)
    }

    fun setRudder(a: Float) {
        Log.d("rudder", a.toString())
        client.sendMessage("rudder", a)
    }

    fun setThrottle(a: Float) {
        Log.d("throttle", a.toString())
        client.sendMessage("throttle", a)
    }

    fun disconnect() {
        client.disconnect()
    }
}