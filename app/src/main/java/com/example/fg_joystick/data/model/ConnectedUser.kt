package com.example.fg_joystick.data.model

/**
 * Data class that captures user information for logged in users retrieved from connectRepository
 */
data class ConnectedUser(
        val userId: String,
        val displayName: String
)