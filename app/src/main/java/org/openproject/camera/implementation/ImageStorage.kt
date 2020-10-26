package org.openproject.camera.implementation

import java.io.OutputStream

typealias IntArrayBuffer = MutableList<Int>

open class ImageStorage: OutputStream() {
    open val dataBuffer: MutableList<ByteArray> =  mutableListOf()
    open val intArray: IntArrayBuffer = mutableListOf()
    override fun write(b: Int) {
        intArray.add(b)
    }

    override fun write(b: ByteArray?) {
        if (b != null) {
            dataBuffer.add(b)
        }
    }

    override fun close(){
        dataBuffer.clear()
        intArray.clear()
        super.close()
    }

}
