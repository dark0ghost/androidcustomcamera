package org.dark0ghost.camera

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.dark0ghost.camera.implementation.GlobalSettings
import org.dark0ghost.camera.implementation.GlobalSettings.server
import org.dark0ghost.camera.implementation.ThreadServer
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestSocketServer() {
    init {
        server = ThreadServer("state",9090,"test"){
            "test"
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