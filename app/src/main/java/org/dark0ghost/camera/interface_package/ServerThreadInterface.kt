package org.dark0ghost.camera.interface_package

import java.net.ServerSocket

interface ServerThreadInterface {
    val socket: ServerSocket

    fun stopServer(): Unit
    fun close(): Unit
}