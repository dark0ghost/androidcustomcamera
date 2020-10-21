package org.openproject.camera.implementation

import io.ktor.application.*
import io.ktor.network.sockets.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.util.*
import io.rsocket.kotlin.RSocketAcceptor
import io.rsocket.kotlin.RSocketRequestHandler
import io.rsocket.kotlin.payload.Payload
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch


open class Server(private val trigger: String,) {
    private lateinit  var serverSocket: RSocketAcceptor

    open fun initSocket(){
        serverSocket  = {
            val data = payload.metadata?.readText() ?: error("Empty metadata")
            RSocketRequestHandler {
                when (data) {
                    "rr" -> requestResponse = {
                        println("Server receive: ${it.data.readText()}")
                        Payload("From server")
                    }
                    "rs" -> requestStream = {
                        println("Server receive: ${it.data.readText()}")
                        flowOf(Payload("From server"))
                    }
                }
            }
        }
    }

    private suspend fun client1() {

        val (clientConnection, serverConnection) = SimpleLocalConnection()

        GlobalScope.launch {
            serverConnection.startServer(acceptor = acceptor)
        }

        val rSocketClient = clientConnection.connectClient()
        rSocketClient.job.join()
        println("Client 1 canceled: ${rSocketClient.job.isCancelled}")
        try {
            rSocketClient.requestResponse(Payload.Empty)
        } catch (e: Throwable) {
            println("Client 1 canceled after creation with: $e")
        }
    }

    @KtorExperimentalAPI
    open fun startServer(){
        embeddedServer(CIO) {
            install(RSocketServerSupport) {
                //configure rSocket server (all values have defaults)

                //install interceptors
                plugin = Plugin(
                        connection = listOf(::SomeConnectionInterceptor)
                )
            }
            //configure routing
            routing {
                //configure route `url:port/rsocket`
                rSocket("rsocket") {
                    RSocketRequestHandler {
                        //handler for request/response
                        requestResponse = { request: Payload ->
                            //... some work here
                            delay(500) // work emulation
                            Payload("data", "metadata")
                        }

                        requestStream = { request: Payload ->
                            flow {
                                repeat(1000) { i ->
                                    emit(Payload("data: $i"))
                                }
                            }
                        }
                    }
                }
            }
        }.start(true)
    }

}