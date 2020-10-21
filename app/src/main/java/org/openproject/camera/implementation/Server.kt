package org.openproject.camera.implementation

import android.media.Image
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

open class Server(private val ip:String, private val port: Int, private val openPort:Int, private val trigger: String) {
    private lateinit var serverSocket: ServerSocket
    private lateinit var clientSocket: Socket
    private lateinit var outData: PrintWriter
    private lateinit var inputDataBuffer: BufferedReader
    private fun startServer(callBack: ()->Image){
          this.outData = PrintWriter(clientSocket.getOutputStream(),true)
          val inputStream = InputStreamReader(clientSocket.getInputStream())
          this.inputDataBuffer = BufferedReader(inputStream)
          val responseServer: String = inputDataBuffer.readLine()
          if (trigger == responseServer){
              outData.println(callBack)
          }
    }
    private fun initSocket(){
        this.serverSocket = ServerSocket(openPort)
        this.clientSocket = serverSocket.accept()
    }

    open fun close(){
        this.clientSocket.close()
        this.serverSocket.close()
        this.inputDataBuffer.close()
        this.outData.close()
    }

    open fun readSocket(callBack: ()->Image){
        this.startServer(callBack)

    }
}