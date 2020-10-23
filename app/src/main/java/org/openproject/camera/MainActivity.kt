package org.openproject.camera


import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.activeandroid.ActiveAndroid
import com.activeandroid.Configuration
import org.openproject.camera.consts.ConstVar
import org.openproject.camera.implementation.GlobalSettings
import org.openproject.camera.implementation.LuminosityAnalyzer
import org.openproject.camera.permission.isAcceptCamera
import org.openproject.camera.permission.requestCameraPermission
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity: AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private val data = ConstVar()
    private lateinit var previews: PreviewView


    private fun takePhoto() {
        if (!GlobalSettings.ramMode) {
            val imageCaptures = imageCapture ?: return
            val photoFile = File(
                outputDirectory,
                SimpleDateFormat(
                    data.fileNameFormat, Locale.US
                ).format(System.currentTimeMillis()) + ".jpg"
            )
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
            imageCaptures.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {
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
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previews.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .build()
            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        Log.d(data.logTag, "Average luminosity: $luma")
                    })
                }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )
            } catch (exc: Exception) {
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
        if (isAcceptCamera(this@MainActivity)) requestCameraPermission(this@MainActivity)
        super.onCreate(savedInstanceState)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val dbConfiguration: Configuration = Configuration.Builder(this@MainActivity).setDatabaseName(
            "settings.db"
        ).create()
        ActiveAndroid.initialize(dbConfiguration)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        if (!isAcceptCamera(this@MainActivity)) startCamera()
        val cameraButton= findViewById<Button>(R.id.MakePhoto)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(data.logTag, "code: $requestCode")
        startCamera()
    }
    override fun onLowMemory() {
        Process.killProcess(Process.myPid())
        super.onLowMemory()
    }

    fun activitySettingStart() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }


}