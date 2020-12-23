package org.dark0ghost.camera.implementation

import java.io.OutputStream

typealias IntArrayBuffer = MutableList<Byte>

open class ImageStorage: OutputStream() {
    open val intArray: IntArrayBuffer = mutableListOf()
    override fun write(b: Int) {
        intArray.add(b.toByte())
    }

    override fun close(){
        intArray.clear()
        super.close()
    }

}


