package org.dark0ghost.camera.implementation

import android.util.Log
import android.util.Size
import androidx.camera.core.CameraInfo
import org.dark0ghost.camera.fn.printTrace
import org.dark0ghost.camera.interface_package.ServerThreadInterface
import java.io.*
import java.net.ServerSocket
import java.net.Socket

open class ThreadServer(private val trigger: List<String>, openPort: Int, private val logTag: String, private val cameraInfo: CameraInfo?, private val updateCamera: () -> Unit, private val callaBack: () -> String): Thread(), ServerThreadInterface {

    private var serverSocket: ServerSocket = ServerSocket(openPort)

    private lateinit var clientSocket: Socket

    private var bufferSender: PrintWriter? = null

    private fun isCommand(message: String): Boolean = message.replace("\n","") in trigger

    private fun runTask(message: String, buffer:  BufferedReader): Unit = when (message){
        "send"->{
            Log.e(logTag, "wait callback")
            val res = callaBack()
            Log.e(logTag, "send data $res")
            bufferSender!!.println(res)
        }
        "set_focus" ->{
            Log.e(logTag, "wait number focus")
            val mes = buffer.readLine()
            val distances = mes.toFloatOrNull()
            if (distances != null) {
                GlobalSettings.manualFocus = distances
                GlobalSettings.isManualFocus = true
                Log.e(logTag, "get focus distance $mes")
                bufferSender!!.println("ok, focus set")
            }else {
                bufferSender!!.println("error: $mes is not float")
            }
            updateCamera()
        }
        "set_size_photo" ->{
            Log.e(logTag, "wait size photo")
            val mes = buffer.readLine()
            val listSize = mes.split(":")
            val castToIntFirstSize = listSize[0].toIntOrNull()
            val castToIntSecondSize = listSize[1].toIntOrNull()
            if(castToIntFirstSize != null && castToIntSecondSize != null){
                GlobalSettings.sizePhoto = Size(castToIntFirstSize,castToIntSecondSize)
                bufferSender!!.println("ok, size photo set")
                updateCamera()
            }else {
                bufferSender!!.println("error size: $castToIntFirstSize:$castToIntSecondSize")
            }
        }
        "get_focus_data" -> {
            bufferSender!!.println(cameraInfo?.zoomState?.value.toString())
        }
        else -> Unit
    }

    private fun runServer(){
        GlobalSettings.isServerStart = true
        Log.e(logTag,"run")
        while (!isInterrupted && !serverSocket.isClosed) {
            try {
                Log.e(logTag, "wait")
                try {
                    clientSocket = serverSocket.accept()
                } catch (e: java.net.SocketException) {
                    continue
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
                    true
                )
                Log.e(logTag, "check")
                val mes = inputData.readLine()
                if (isCommand(mes)) {
                    runTask(mes, inputData)
                } else {
                    bufferSender!!.println("error: Unknown command $mes")
                    Log.e(logTag, "error: Unknown command $mes")
                }

            } catch (e: Exception) {
                printTrace(e)
            }
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
        if(!serverSocket.isClosed)
           serverSocket.close()
        GlobalSettings.isPortBind = false
        GlobalSettings.isServerStart = false
        Log.e(logTag, "Server is end work")
    }

}