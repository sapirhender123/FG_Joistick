package com.example.fg_joystick.data

import com.example.fg_joystick.data.model.ConnectedUser

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of connect status and user credentials information.
 */

class ConnectionRepository(val dataSource: ConnectionDataSource) {

    // in-memory cache of the loggedInUser object
    var user: ConnectedUser? = null
        private set

    val isConnected: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.disconnect()
    }

    fun connect(ip: String, port: String): Result<ConnectedUser> {
        // handle connect
        val result = dataSource.connect(ip, port)

        if (result is Result.Success) {
            setConnectedUser(result.data)
        }

        return result
    }

    private fun setConnectedUser(connectedUser: ConnectedUser) {
        this.user = connectedUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}