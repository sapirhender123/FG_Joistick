package com.fg_joystick.fg_joystick.data

import com.fg_joystick.fg_joystick.data.model.ConnectedUser
import java.io.IOException
import java.net.Socket

/**
 * Class that handles authentication w/ connect credentials and retrieves user information.
 */
class ConnectionDataSource {

    var client: Socket? = null

    fun connect(ip: String, portStr: String): Result<ConnectedUser> {
        return try {
            // TODO: Connect to FlightGear and move to JoyStick View
            // TODO: Do in a new thread
            // this.client = Socket(ip, portStr.toInt());

            val user = ConnectedUser(
                java.util.UUID.randomUUID().toString(), "Connecting to %s:%s".format(ip, portStr)
            )
            Result.Success(user)
        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
    }

    fun disconnect() {
        this.client?.close()
    }
}