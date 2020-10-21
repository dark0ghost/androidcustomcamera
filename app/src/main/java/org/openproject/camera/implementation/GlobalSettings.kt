package org.openproject.camera.implementation

import kotlin.properties.Delegates

object GlobalSettings {
   private val pathToDB: String = ""

   var ramMode: Boolean = false
   var openPort by Delegates.notNull<Int>()
   lateinit var apiString: String

   fun loadConfig(): Unit {

   }
}