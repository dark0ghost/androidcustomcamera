package org.openproject.camera.implementation

import android.util.Log
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


open class Server(private val trigger: String, ip: String, openPort:Int, private val logTag: String, private val callaBack: ()-> ByteArray) {
    @KtorExperimentalAPI
    private val server: ServerSocket =
            aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(ip, openPort) {
            }
    private lateinit var clientSocket: Socket

    @KtorExperimentalAPI
    fun start() {
        Log.e(logTag, "Started echo telnet server at ${server.localAddress}")
        GlobalScope.launch {
            while (true) {
                clientSocket = server.accept()

                println("Socket accepted: ${clientSocket.remoteAddress}")
                val input = clientSocket.openReadChannel()
                val output = clientSocket.openWriteChannel(autoFlush = true)
                var response = ""
                input.read(0) {
                    response += it.array().toString()
                }
                if (response == trigger) {
                    for (i in callaBack())
                        output.writeByte(i)

                }
            }
        }
    }

    @KtorExperimentalAPI
    suspend fun close() {
        this.server.awaitClosed()
        if (!clientSocket.isClosed) {
            clientSocket.awaitClosed()
        }
    }
}
