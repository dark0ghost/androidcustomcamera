package org.openproject.camera.implementation
import android.util.Log
import org.openproject.camera.fn.printTrace
import org.openproject.camera.interface_package.ServerThreadInterface
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

open class ThreadServer(private val trigger: String, private val ip: String, openPort: Int, private val logTag: String, private val callaBack: () -> String): Thread(), ServerThreadInterface {
    private var serverSocket: ServerSocket = ServerSocket(openPort)
    private lateinit var clientSocket: Socket
    private var isStop: Boolean = false
    private var bufferSender: PrintWriter? = null
    private fun isCommand(message: String): Boolean = message == trigger

    private fun runServer(){
        GlobalSettings.isServerStart = true
        try {
            while (!isStop){
                clientSocket = serverSocket.accept()
                Log.e(logTag,"connect")
                val inputData = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                for (i in inputData.readLines())
                if (isCommand(i)){
                    if (bufferSender != null && !bufferSender!!.checkError()) {
                        bufferSender!!.println(callaBack())
                        bufferSender!!.flush()
                    }
                }
            }
        }catch (e: Exception){
            printTrace(e)
        }
    }

    override val socket
        get() = serverSocket

    override fun stopServer(): Unit {
        isStop = true
        this.close()
    }

    override fun run() {
        Log.e(logTag, "Started echo telnet server at ${serverSocket.localSocketAddress}")
        this.isStop = false
        super.run()
        this.runServer()
    }

    override fun close() {
       if(!serverSocket.isClosed){
           serverSocket.close()
           GlobalSettings.isServerStart = false
       }
    }

}