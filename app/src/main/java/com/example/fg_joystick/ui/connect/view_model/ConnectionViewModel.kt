package com.example.fg_joystick.ui.connect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.example.fg_joystick.data.ConnectionRepository
import com.example.fg_joystick.data.Result

import com.example.fg_joystick.R
import kotlin.math.pow

class ConnectionViewModel(private val connectionRepository: ConnectionRepository) : ViewModel() {

    private val _connectionForm = MutableLiveData<ConnectionFormState>()
    val connectionFormState: LiveData<ConnectionFormState> = _connectionForm

    private val _connectionResult = MutableLiveData<ConnectionResult>()
    val connectionResult: LiveData<ConnectionResult> = _connectionResult

    fun connect(username: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = connectionRepository.connect(username, password)

        if (result is Result.Success) {
            _connectionResult.value = ConnectionResult(success = ConnectedUserView(displayName = result.data.displayName))
        } else {
            _connectionResult.value = ConnectionResult(error = R.string.connection_failed)
        }
    }

    fun connectionDataChanged(ip: String, port: String) {
        if (!isIPValid(ip)) {
            _connectionForm.value = ConnectionFormState(ipError = R.string.invalid_ip)
        } else if (!isPortValid(port)) {
            _connectionForm.value = ConnectionFormState(portError = R.string.invalid_port)
        } else {
            _connectionForm.value = ConnectionFormState(isDataValid = true)
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

        val port = portStr.toInt()
        return 0 < port && port < 2.0.pow(16.0) - 1
    }
}