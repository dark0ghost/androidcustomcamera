package org.dark0ghost.camera.implementation

import java.io.OutputStream

private typealias IntArrayBuffer = MutableList<Int>

open class ImageStorage: OutputStream() {


    private fun IntArrayBuffer.toStrings(): String {
        TODO()
    }

    open val intArray: IntArrayBuffer = mutableListOf()

    override fun write(b: Int) {
        intArray.add(b)
    }

    override fun close() {
        intArray.clear()
        super.close()
    }

    fun getDataAndClose(): String {
        val response = intArray.toString()
        close()
        return response
    }

}


