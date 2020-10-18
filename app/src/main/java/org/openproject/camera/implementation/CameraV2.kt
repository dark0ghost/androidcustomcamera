package org.openproject.camera.implementation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.media.ImageReader
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import org.openproject.camera.fn.printTrace
import org.openproject.camera.permission.requestCameraPermission
import java.io.File
import java.util.concurrent.Executor

class CameraV2(private var cameraManager: CameraManager?, private var cameraId: String, context: Context, private val imageView: TextureView, nameFile: String ) {
    private var cameraDevice: CameraDevice? = null
    private val logTag: String = org.openproject.camera.consts.Consts().logTag
    private lateinit var captureSession: CameraCaptureSession
    private lateinit var imageReader: ImageReader
    private lateinit var builder: CaptureRequest.Builder
    private lateinit var surfaceTextureView: SurfaceTexture
    private var file: File = File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM), nameFile)
    private val imageListener = ImageReader.OnImageAvailableListener { reader ->
        backgroundHandler!!.post(ImageSaver(reader.acquireLatestImage(), this.file))
    }
    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener{
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            surfaceTextureView = imageView.surfaceTexture!!
            println("logggg")
            createCameraPreviewSession()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
            TODO("Not yet implemented")
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            TODO("Not yet implemented")
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            val time: Long =  System.currentTimeMillis()
            var bitmap: Bitmap = Bitmap.createBitmap(1920,1080,Bitmap.Config.ARGB_8888)
            bitmap = imageView.getBitmap(bitmap)
            val pixelArray = IntArray(bitmap.byteCount)
            bitmap.getPixels(pixelArray,0,1920,0,0,1920,1080)
            // imageProcessStream.pushImage(pixelArray,time/1000L)
        }

    }
    private val cameraCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        @RequiresApi(Build.VERSION_CODES.P)
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            imageReader = ImageReader.newInstance(1920,1080, ImageFormat.JPEG,1)
            imageView.surfaceTextureListener = surfaceTextureListener
            imageReader.setOnImageAvailableListener(imageListener,null)
            surfaceTextureView = imageView.surfaceTexture!!
            println("logggg")
            createCameraPreviewSession()

        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice?.close()
            Log.i(logTag, "disconnect camera  with id:" + (cameraDevice?.id ?: 1))
            cameraDevice = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Log.i(logTag, "error! camera id:${camera.id} error:$error")
        }
    }

    var backgroundHandler: Handler? = null
    fun isOpen(): Boolean = this.cameraDevice != null

    fun close() {
        this.cameraDevice?.close()
        this.cameraDevice = null
    }

    fun openCamera(context: Context) {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestCameraPermission(context as Activity)
            }
            cameraManager?.openCamera(cameraId, cameraCallback, null)
        } catch (e: CameraAccessException) {
            e.message?.let {
                Log.e(this.logTag, it)
            }
        }
    }

    fun makePhoto() {
        try {
            if (cameraDevice == null) return
            val captureBuilder: CaptureRequest.Builder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            val captureCallback: CameraCaptureSession.CaptureCallback = object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
                }
            }
            captureSession.stopRepeating()
            captureSession.abortCaptures()
            captureSession.capture(captureBuilder.build(), captureCallback, backgroundHandler)
        } catch (e: CameraAccessException) {
            printTrace(e)
        }
    }

    private fun createCameraPreviewSession(): Boolean {
        surfaceTextureView.setDefaultBufferSize(640, 480)
        val surface = Surface(surfaceTextureView)
        try {
            builder.addTarget(surface)
            val executor = Executor { command ->
                command?.run()
            }
            val listOutputConfiguration: List<OutputConfiguration> = listOf(OutputConfiguration(surface), OutputConfiguration(imageReader.surface))

            val config = SessionConfiguration(SessionConfiguration.SESSION_REGULAR, listOutputConfiguration, executor, object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    captureSession = session
                    try {
                        captureSession.setRepeatingBurst(listOf(builder.build()), null, backgroundHandler)
                    } catch (e: CameraAccessException) {
                        printTrace(e)
                    }
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    print(session)

                }
            })
            cameraDevice!!.createCaptureSession(config)
            return true

        } catch (e: CameraAccessException) {
            printTrace(e)
            return false
        }
    }
}