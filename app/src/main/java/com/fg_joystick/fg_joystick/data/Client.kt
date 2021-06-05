package com.fg_joystick.fg_joystick.data

import android.util.Log
import com.fg_joystick.fg_joystick.data.model.ConnectedUser
import java.io.IOException
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*
import java.util.concurrent.*

/**
 * Create a socket and connect to flight-gear. Send commands from user to flight-gear
 */
object Client {

    @Volatile
    private var socket: Socket? = null
    @Volatile
    private var writer: PrintWriter? = null
    @Volatile
    private var stop = false
    @Volatile
    private var message: String? = null

    private var mainThread: ExecutorService? = null
    private var threadPool: ExecutorService? = null

    init {
        Log.d("Client",  "Model invoked")
    }

    // connect to flight-gear
    fun connect(ip: String, portStr: String): Result<ConnectedUser> {

        // create thread to open the socket
        mainThread = Executors.newSingleThreadExecutor()

        return mainThread!!.submit(Callable {
            return@Callable try {
                // open the socket
                socket = Socket()
                socket!!.connect(InetSocketAddress(ip, portStr.toInt()), 1000)
                writer = PrintWriter(socket!!.getOutputStream())

                // open thread pool to write commands to flight-gear
                threadPool = Executors.newFixedThreadPool(5)
                threadPool!!.submit{
                    // TODO : no busy waiting
                    while (!stop) {
                        synchronized(this)
                        {
                            // wait for a new message and send to flight-gear
                            if (message != null) {
                                writer!!.printf(message!!)
                                writer!!.flush()
                                message = null
                            }
                        }

                    }
                }
                // connection succeeded
                val user = ConnectedUser(
                    UUID.randomUUID().toString(),
                    "Connecting to %s:%s".format(ip, portStr)
                )
                Result.Success(user)
            } catch (e: Throwable) {
                // connecting failed
                Result.Error(IOException("Error logging in", e))
            }
        }).get()
    }

    // send massage to flight-gear
    fun sendMessage(attr: String, value: Float) {
        val path = when (attr) {
            "throttle" -> "engines/current-engine/throttle"
            else -> "flight/$attr"
        }

        synchronized(this) {
            message = "set /controls/$path $value\r\n"
        }
    }

    // disconnect from flight gear and stop thread pool
    fun disconnect() {
        synchronized (this) {
            stop = true
        }
        writer!!.close()
        socket!!.close()
        threadPool!!.shutdown()
        threadPool!!.awaitTermination(30, TimeUnit.SECONDS)
        mainThread!!.shutdown()
        mainThread!!.awaitTermination(30, TimeUnit.SECONDS)
    }
}
