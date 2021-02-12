package org.dark0ghost.camera.implementation
import android.util.Log
import org.dark0ghost.camera.fn.printTrace
import org.dark0ghost.camera.interface_package.ServerThreadInterface
import java.io.*
import java.net.ServerSocket
import java.net.Socket

open class ThreadServer(private val trigger: String, openPort: Int, private val logTag: String, private val callaBack: () -> String): Thread(), ServerThreadInterface {

    private var serverSocket: ServerSocket = ServerSocket(openPort)

    private lateinit var clientSocket: Socket

    private var bufferSender: PrintWriter? = null

    private fun isCommand(message: String): Boolean = message == trigger

    private fun runServer(){
        GlobalSettings.isServerStart = true
        Log.e(logTag,"run")
        try {
                while (!isInterrupted && !serverSocket.isClosed) {
                    Log.e(logTag, "wait")
                    try {
                    clientSocket = serverSocket.accept()
                    }catch (e: java.net.SocketException){
                        return
                    }
                    Log.e(logTag, "connect")
                    val inputStream = clientSocket.getInputStream()
                    val inputData = BufferedReader(InputStreamReader(inputStream))
                    bufferSender = PrintWriter(
                            BufferedWriter(
                                    OutputStreamWriter(
                                            clientSocket.getOutputStream()
                                    )
                            ),
                            true)
                    Log.e(logTag, "check")
                    if (isCommand(inputData.readLine())) {
                        bufferSender!!.println(callaBack())
                        bufferSender!!.flush()
                        Log.e(logTag, "send")
                    } else {
                        bufferSender!!.println("no")
                        bufferSender!!.flush()
                    }
                }
            } catch (e: Exception){
                printTrace(e)
            }
        }

    init{
        GlobalSettings.isPortBind = true
    }

    open var isPhotoSave: Boolean = false

    override val socket
        get() = serverSocket


    override fun stopServer() {
        interrupt()
        this.close()
    }

    override fun run() {
        Log.e(logTag, "Started echo telnet server at ${serverSocket.localSocketAddress}")
        this.runServer()
        super.run()
    }

    override fun close() {
        if(!serverSocket.isClosed){
           serverSocket.close()
        }
        GlobalSettings.isPortBind = false
        GlobalSettings.isServerStart = false
        Log.e(logTag, "Server is end work")
    }

}