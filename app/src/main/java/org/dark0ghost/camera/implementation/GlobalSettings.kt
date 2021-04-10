package org.dark0ghost.camera.implementation

import android.util.Size

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

   var sizePhoto: Size = Size(600,800)

   lateinit var server: ThreadServer
}