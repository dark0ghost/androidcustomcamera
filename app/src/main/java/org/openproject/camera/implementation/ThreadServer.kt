package org.openproject.camera.implementation
import android.util.Log
import org.openproject.camera.fn.printTrace
import org.openproject.camera.interface_package.ServerThreadInterface
import java.io.*
import java.net.ServerSocket
import java.net.Socket

open class ThreadServer(private val trigger: String, private val ip: String, openPort: Int, private val logTag: String, private val callaBack: () -> String): Thread(), ServerThreadInterface {
    private var serverSocket: ServerSocket = ServerSocket(openPort)
    private lateinit var clientSocket: Socket
    private var isStop: Boolean = false
    private var bufferSender: PrintWriter? = null
    private fun isCommand(message: String): Boolean = message == trigger
    open var isPhotoSave:Boolean = false

    private fun runServer(){
        GlobalSettings.isServerStart = true
        Log.e(logTag,"run")
        try {
            while (!isStop){
                Log.e(logTag,"wait")
                clientSocket = serverSocket.accept()
                Log.e(logTag,"connect")
                val inputStream = clientSocket.getInputStream()
                val inputData = BufferedReader(InputStreamReader(inputStream))
                bufferSender = PrintWriter(
                        BufferedWriter(
                            OutputStreamWriter(
                                clientSocket.getOutputStream()
                            )
                        ),
                        true)
                Log.e(logTag,"check")
                if (isCommand(inputData.readLine())){
                    bufferSender!!.println(callaBack())
                    bufferSender!!.flush()
                    Log.e(logTag,"send")
                }else{
                    bufferSender!!.println("no")
                    bufferSender!!.flush()
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
        this.runServer()
        super.run()
    }

    override fun close() {
       if(!serverSocket.isClosed){
           serverSocket.close()
           GlobalSettings.isServerStart = false
       }
    }

}