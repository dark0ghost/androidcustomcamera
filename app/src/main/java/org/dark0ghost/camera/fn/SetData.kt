package org.dark0ghost.camera.fn

import android.content.SharedPreferences
import org.dark0ghost.camera.consts.ConstVar
import org.dark0ghost.camera.settings.GlobalSettings

fun setGlobalSettingsFromContext(prefs: SharedPreferences,data: ConstVar = ConstVar() ) {
    GlobalSettings.startServer = prefs.getBoolean(data.startServerTag, false)
    GlobalSettings.ramMode = prefs.getBoolean(data.ramModeTag, false)
    GlobalSettings.isRangeFinderStart = prefs.getBoolean(data.isRangeFinderStartTag, false)
    GlobalSettings.port = prefs.getInt(data.portTag, 4290)
}



fun setNewPref(prefs: SharedPreferences,data: ConstVar = ConstVar()): SharedPreferences.Editor {
    val editor: SharedPreferences.Editor = prefs.edit()
    editor
        .putBoolean(data.startServerTag, GlobalSettings.startServer)
        .putBoolean(data.ramModeTag, GlobalSettings.ramMode)
        .putBoolean(data.isRangeFinderStartTag, GlobalSettings.isRangeFinderStart)
        .putInt(data.portTag, GlobalSettings.port)
        .apply()
    return editor
}