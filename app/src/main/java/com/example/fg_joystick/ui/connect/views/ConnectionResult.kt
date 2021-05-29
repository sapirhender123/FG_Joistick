package com.example.fg_joystick.ui.connect

/**
 * Authentication result : success (user details) or error message.
 */
data class ConnectionResult(
        val success: ConnectedUserView? = null,
        val error: Int? = null
)