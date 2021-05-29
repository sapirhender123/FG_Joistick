package com.example.fg_joystick.ui.connect

/**
 * Data validation state of the connect form.
 */
data class ConnectionFormState(val ipError: Int? = null,
                               val portError: Int? = null,
                               val isDataValid: Boolean = false)