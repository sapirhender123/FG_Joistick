package com.fg_joystick.fg_joystick.ui.connect.view

interface OnJoystickChange {
    fun onChange(a: Float, e: Float)
}

class Joystick {
    var onChange: OnJoystickChange? = null
}