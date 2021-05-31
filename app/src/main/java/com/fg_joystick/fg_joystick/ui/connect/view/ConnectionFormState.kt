package com.fg_joystick.fg_joystick.ui.connect.view

/**
 * Data validation state of the connect form.
 */
data class ConnectionFormState(val ipError: Int? = null,
                               val portError: Int? = null,
                               val isDataValid: Boolean = false)