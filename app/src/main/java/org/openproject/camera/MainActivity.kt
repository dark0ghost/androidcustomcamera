package org.openproject.camera


import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import org.openproject.camera.consts.ConstVar
import org.openproject.camera.implementation.LuminosityAnalyzer
import org.openproject.camera.permission.isAcceptCamera
import org.openproject.camera.permission.requestCameraPermission
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class MainActivity: AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private val data = ConstVar()
    private lateinit var previews: PreviewView
    private lateinit var container: FrameLayout
    private var preview: Preview? = null

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(
                outputDirectory,
                SimpleDateFormat(data.fileNameFormat, Locale.US
                ).format(System.currentTimeMillis()) + ".jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
                outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e(data.logTag, "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                val msg = "Photo capture succeeded: $savedUri"
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                Log.d(data.logTag, msg)
            }
        })
    }

    private fun startCamera() {
        val metrics = DisplayMetrics().also {
            previews.display.getRealMetrics(it)
        }
        Log.d(data.logTag, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)

        val rotation = previews.display.rotation
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().setTargetAspectRatio(rotation)

            imageCapture = ImageCapture.Builder()
                    .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                    .build()
                    .also { it->
                        it.setAnalyzer(cameraExecutor, LuminosityAnalyzer {luma->
                            Log.d(data.logTag, "Average luminosity: $luma")
                        })
                    }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(data.logTag, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!isAcceptCamera(this@MainActivity)) requestCameraPermission(this@MainActivity)
        startCamera()
        val cameraButton= findViewById<Button>( R.id.MakePhoto)
        previews = findViewById(R.id.viewFinder)
        cameraButton.setOnClickListener {
                takePhoto()
        }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()


    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


}