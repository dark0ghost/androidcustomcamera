package org.openproject.camera.implementation

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers


open class Server(private val trigger: String,var ip: String) {
    private lateinit var socket: ServerSocket

    @KtorExperimentalAPI
    open fun initSocket(openPort: Int){
        socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(ip,openPort)
    }

}