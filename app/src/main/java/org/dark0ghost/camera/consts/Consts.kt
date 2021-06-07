package org.dark0ghost.camera.consts

import android.Manifest

data class ConstVar(var logTag: String = "CameraLogs",var fileNameFormat: String = "yyyy-MM-dd-HH-mm-ss-SSS",val requestCodePermission: Int = 10,val requiredPermissions: Array<String> = arrayOf(
    Manifest.permission.CAMERA),val ramModeTag: String = "ram_mode_tag", val portTag: String = "port_tag",val startServerTag: String = "start_server_tag",val triggerTag: String = "trigger_tag",val isServerStartTag: String = "is_server_start_tag",val isRangeFinderStartTag: String = "is_range_finder_start_tag") {
    private val hashConst: Int = 31
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ConstVar
        return !(logTag != other.logTag && fileNameFormat != other.fileNameFormat && requestCodePermission != other.requestCodePermission && !requiredPermissions.contentEquals(
            other.requiredPermissions
        ))
    }

    override fun hashCode(): Int {
        var result = logTag.hashCode()
        result += hashConst * result + fileNameFormat.hashCode()
        result += hashConst * result + requestCodePermission
        result += hashConst * result + requiredPermissions.contentHashCode()
        return result
    }

    override fun toString(): String {
        return """
            {
            log:$logTag,
            format:$fileNameFormat,
            code permission:$requestCodePermission,
            required permissions$requiredPermissions,
            }
        """.trimIndent()
    }
}


