package com.fg_joystick.fg_joystick.ui.connect.view

// Using strategy pattern
interface OnJoystickChange {
    fun onChange(a: Float, e: Float)
}

class Joystick {
    // Reusable Joystick
    var onChange: OnJoystickChange? = null
}