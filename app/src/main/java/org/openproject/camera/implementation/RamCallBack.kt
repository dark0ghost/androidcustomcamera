package org.openproject.camera.implementation

import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException

class RamCallBack( private val data: String, private val callBackOnSavedMessage: (output: ImageCapture.OutputFileResults) -> Unit = {}) : ImageCapture.OnImageSavedCallback {
    override fun onError(exc: ImageCaptureException) {
        Log.e(data, "Photo capture failed: ${exc.message}", exc)
    }

    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
        callBackOnSavedMessage(output)
        Log.d(data, output.savedUri.toString())
    }
}