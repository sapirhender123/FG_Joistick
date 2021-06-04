package com.fg_joystick.fg_joystick.ui.connect.view_model

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fg_joystick.fg_joystick.R
import com.fg_joystick.fg_joystick.data.Client
import com.fg_joystick.fg_joystick.data.Result
import com.fg_joystick.fg_joystick.ui.connect.view.ConnectedUserView
import com.fg_joystick.fg_joystick.ui.connect.view.ConnectionFormState
import com.fg_joystick.fg_joystick.ui.connect.view.ConnectionResult
import kotlin.math.pow


class ConnectionViewModel(private val client: Client) : ViewModel() {

    private val _connectionForm = MutableLiveData<ConnectionFormState>()
    val connectionFormState: LiveData<ConnectionFormState> = _connectionForm

    private val _connectionResult = MutableLiveData<ConnectionResult>()
    val connectionResult: LiveData<ConnectionResult> = _connectionResult

    fun connect(ip: String, port: String) {
        // can be launched in a separate asynchronous job
        val result = client.connect(ip, port)

        if (result is Result.Success) {
            _connectionResult.value =
                ConnectionResult(
                    success = ConnectedUserView(
                        displayName = result.data.displayName
                    )
                )
        } else {
            _connectionResult.value =
                ConnectionResult(error = R.string.connection_failed)
        }
    }

    fun connectionDataChanged(ip: String, port: String) {
        if (!isIPValid(ip)) {
            _connectionForm.value =
                ConnectionFormState(
                    ipError = R.string.invalid_ip
                )
        } else if (!isPortValid(port)) {
            _connectionForm.value =
                ConnectionFormState(
                    portError = R.string.invalid_port
                )
        } else {
            _connectionForm.value =
                ConnectionFormState(
                    isDataValid = true
                )
        }
    }

    // A placeholder username validation check
    private fun isIPValid(ip: String): Boolean {
        return Patterns.IP_ADDRESS.matcher(ip).matches()
    }

    // A placeholder password validation check
    private fun isPortValid(portStr: String): Boolean {
        if (portStr.isEmpty()) {
            return false
        }

        val port : Int = portStr.toIntOrNull() ?: return false
        return 0 < port!! && port < 2.0.pow(16.0) - 1
    }
}