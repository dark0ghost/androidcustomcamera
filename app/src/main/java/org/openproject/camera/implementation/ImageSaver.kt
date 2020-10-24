package org.openproject.camera.implementation

import android.media.Image
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

class ImageSaver(private var image: Image, private var file: File): Runnable {
    override fun run(){
        val arrayByte: ByteBuffer? = image.planes[0].buffer
        if (arrayByte != null) {
            val byteBuffer: ByteArray = byteArrayOf(arrayByte.remaining().toByte())
            arrayByte.get(byteBuffer)
            val out: FileOutputStream = FileOutputStream(file)
            out.write(byteBuffer)
            out.close()
        }
    }
}
