package org.openproject.camera.implementation

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


open class Server(private val trigger: String,private val ip: String,private val openPort:Int) {
    @KtorExperimentalAPI
    private val server: ServerSocket =
        aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(ip, openPort)
    private lateinit var clientSocket: Socket

    @KtorExperimentalAPI
    suspend fun start() = coroutineScope {
        while (true) {
            clientSocket = server.accept()
            launch {
                println("Socket accepted: ${clientSocket.remoteAddress}")
                val input = clientSocket.openReadChannel()
                val output = clientSocket.openWriteChannel(autoFlush = true)
                try {
                    while (true) {
                        val line = input.readUTF8Line(1000)
                        println("${clientSocket.remoteAddress}: $line")
                       // output.write(1,"$line\r\n")
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    clientSocket.awaitClosed()
                }
            }
        }

    }
    open fun startThread(){

    }


    @KtorExperimentalAPI
    suspend fun close(){
        this.server.awaitClosed()
        if (!clientSocket.isClosed){
            clientSocket.awaitClosed()
        }
    }
}