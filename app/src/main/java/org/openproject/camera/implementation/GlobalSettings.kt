package org.openproject.camera.implementation

import kotlin.properties.Delegates

object GlobalSettings {

   var ramMode: Boolean = false
   var port = 5050
   var ip: String = "0.0.0.0"
   lateinit var apiString: String
   var startServer: Boolean = true
   var trigger: String = "send photo"

   fun loadConfig(): Unit {

   }
}