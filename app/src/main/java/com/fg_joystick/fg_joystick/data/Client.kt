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
 * Class that handles authentication w/ connect credentials and retrieves user information.
 * Kotlin’s representation of a Singleton class requires the object keyword only.
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

    fun connect(ip: String, portStr: String): Result<ConnectedUser> {

        mainThread = Executors.newSingleThreadExecutor()

        return mainThread!!.submit(Callable {
            return@Callable try {
                socket = Socket()
                socket!!.connect(InetSocketAddress(ip, portStr.toInt()), 1000)
                writer = PrintWriter(socket!!.getOutputStream())

                threadPool = Executors.newFixedThreadPool(5)
                threadPool!!.submit{
                    // TODO : no busy waiting
                    while (!stop) {
                        synchronized(this)
                        {
                            if (message != null) {
                                writer!!.printf(message!!)
                                writer!!.flush()
                                message = null
                            }
                        }

                    }
                }
                val user = ConnectedUser(
                    UUID.randomUUID().toString(),
                    "Connecting to %s:%s".format(ip, portStr)
                )
                Result.Success(user)
            } catch (e: Throwable) {
                Result.Error(IOException("Error logging in", e))
            }
        }).get()
    }

    fun sendMessage(attr: String, value: Float) {
        val path = when (attr) {
            "throttle" -> "engines/current-engine/throttle"
            else -> "flight/$attr"
        }

        synchronized(this) {
            message = "set /controls/$path $value\r\n"
        }
    }

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
