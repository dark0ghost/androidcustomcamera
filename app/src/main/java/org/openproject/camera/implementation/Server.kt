package org.openproject.camera.implementation

import android.util.Log
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


open class Server(private val trigger: String,private val ip: String,private val openPort:Int,private val logTag: String,private val callaback:()-> Byte) {
    @KtorExperimentalAPI
    private val server: ServerSocket =
        aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(ip, openPort)
    private lateinit var clientSocket: Socket

    @KtorExperimentalAPI
    suspend fun start() = coroutineScope {
        Log.e(logTag,"Started echo telnet server at ${server.localAddress}")
        while (true) {
            clientSocket = server.accept()
            launch {
                println("Socket accepted: ${clientSocket.remoteAddress}")
                val input = clientSocket.openReadChannel()
                val output = clientSocket.openWriteChannel(autoFlush = true)
                var response = ""
                input.read(0){
                    response += it.array().toString()
                }
                if (response == trigger){
                    output.writeByte(callaback())
                }
            }
        }

    }

    @KtorExperimentalAPI
    suspend fun close(){
        this.server.awaitClosed()
        if (!clientSocket.isClosed){
            clientSocket.awaitClosed()
        }
    }
}