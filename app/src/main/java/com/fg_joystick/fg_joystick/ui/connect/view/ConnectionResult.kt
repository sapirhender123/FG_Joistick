package com.fg_joystick.fg_joystick.ui.connect.view

/**
 * Authentication result : success (user details) or error message.
 */
data class ConnectionResult(
        val success: ConnectedUserView? = null,
        val error: Int? = null
)