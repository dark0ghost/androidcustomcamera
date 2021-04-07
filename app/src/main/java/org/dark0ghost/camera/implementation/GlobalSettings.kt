package org.dark0ghost.camera.implementation

object GlobalSettings {
   var ramMode: Boolean = false

   var port = 4290

   var ip: String = "0.0.0.0"

   var startServer: Boolean = true

   var trigger: String = "send"

   var isServerStart: Boolean = false

   var ipServer: String = "$ip:$port"

   var isRangeFinderStart: Boolean = false

   var isPortBind: Boolean = false

   lateinit var server: ThreadServer
}