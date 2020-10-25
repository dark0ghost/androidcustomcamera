package org.openproject.camera.implementation

import java.io.OutputStream

open class ImageStorage: OutputStream() {
    open val dataBuffer: MutableList<Int> =  mutableListOf()
    override fun write(b: Int) {
        dataBuffer.add(b)
    }

}