package org.dark0ghost.camera

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.dark0ghost.camera.settings.GlobalSettings
import org.dark0ghost.camera.settings.GlobalSettings.server
import org.dark0ghost.camera.server.ThreadServer
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream

@RunWith(AndroidJUnit4::class)
class TestSocketServer() {
    init {
        server = ThreadServer(listOf("state"),9090,"test", null, {}){
            ByteArrayOutputStream()
        }
    }
    @Test
    fun testStartServer(){
        server.start()
        Thread.sleep(100)
        assert(GlobalSettings.isServerStart)
    }

    @Test
    fun testStopServer(){
        server.stopServer()
        assert(!GlobalSettings.isServerStart)
    }
}