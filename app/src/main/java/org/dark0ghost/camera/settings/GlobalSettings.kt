package org.dark0ghost.camera.settings

import android.util.Size
import org.dark0ghost.camera.server.ThreadServer

object GlobalSettings {
   var ramMode: Boolean = false

   var port = 4290

   var ip: String = "0.0.0.0"

   var startServer: Boolean = true

   val trigger: List<String> = listOf("send", "set_focus", "set_size_photo", "get_focus_data")

   var isServerStart: Boolean = false

   var ipServer: String = "$ip:$port"

   var isRangeFinderStart: Boolean = false

   var isPortBind: Boolean = false

   var debugSavePhotoMode: Boolean = true

   var sizePhoto: Size = Size(600, 800)

   var isManualFocus: Boolean = false

   var manualFocus: Float = 100f

   lateinit var server: ThreadServer
}