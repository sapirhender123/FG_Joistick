package com.fg_joystick.fg_joystick.data

import com.fg_joystick.fg_joystick.data.model.ConnectedUser
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.*

/**
 * Class that handles authentication w/ connect credentials and retrieves user information.
 */
class Client {

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


    fun connect(ip: String, portStr: String): Result<ConnectedUser> {

        mainThread = Executors.newSingleThreadExecutor()
        val future : Future<Result<ConnectedUser>>? = mainThread!!.submit(Callable {
            return@Callable  try {
                socket = Socket(ip, portStr.toInt())
                // TODO : how do you know if ip and socket correct?
                writer = PrintWriter(socket!!.getOutputStream())

                threadPool = Executors.newFixedThreadPool(5)
                threadPool!!.submit(Runnable {
                    // TODO : no busy waiting
                        while (!stop)
                        {
                            synchronized (this)
                            {
                                if (message != null) {
                                    writer!!.printf(message)
                                    message = null
                                }
                            }

                        }
                })
                val user = ConnectedUser(
                    java.util.UUID.randomUUID().toString(),
                    "Connecting to %s:%s".format(ip, portStr)
                )
                Result.Success(user)
            } catch (e: Throwable){
                Result.Error(IOException("Error logging in", e))
            }
        })

        return future!!.get()
    }

    fun sendMessage(attr: String, value: Float) {
        var path : String = attr
        if (attr == "throttle") {
            path = "current-engine/$attr"
        }
        synchronized(this) {
            message = "set /controls/flight/$path ${value.toString()}"
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
