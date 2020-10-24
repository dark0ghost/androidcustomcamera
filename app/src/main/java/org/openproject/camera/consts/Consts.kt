package org.openproject.camera.consts

import android.Manifest

data class ConstVar(val logTag: String = "CameraLogs",val fileNameFormat: String = "yyyy-MM-dd-HH-mm-ss-SSS",val requestCodePermission: Int = 10,val requiredPermissions: Array<String> = arrayOf(
    Manifest.permission.CAMERA), val DATABASE_VERSION: Int = 1, val DATABASE_NAME: String = "settings.db") {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ConstVar
        return !(logTag != other.logTag && fileNameFormat != other.fileNameFormat && requestCodePermission != other.requestCodePermission && !requiredPermissions.contentEquals(other.requiredPermissions))
    }

    override fun hashCode(): Int {
        var result = logTag.hashCode()
        result = 31 * result + fileNameFormat.hashCode()
        result = 31 * result + requestCodePermission
        result = 31 * result + requiredPermissions.contentHashCode()
        return result
    }

    override fun toString(): String {
        return """
            {
            log:$logTag,
            format:$fileNameFormat,
            code premission:$requestCodePermission
            required permissions$requiredPermissions
            }
        """.trimIndent()
    }
}


