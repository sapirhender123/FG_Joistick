package com.example.fg_joystick.data

import com.example.fg_joystick.data.model.ConnectedUser
import java.io.IOException
import java.net.Socket

/**
 * Class that handles authentication w/ connect credentials and retrieves user information.
 */
class ConnectionDataSource {

    fun connect(ip: String, portStr: String): Result<ConnectedUser> {
        try {
            // TODO: handle loggedInUser authentication
            val client: Socket = Socket(ip, portStr.toInt());
            client.outputStream.write("Hello from the client!".toByteArray())
            client.close();

            // TODO: Connect to FlightGear and move to JoyStick View
            val fakeUser = ConnectedUser(
                java.util.UUID.randomUUID().toString(), "Connecting to %s:%s".format(ip, portStr)
            )
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun disconnect() {
        // TODO: Disconnect from FlightGear
    }
}