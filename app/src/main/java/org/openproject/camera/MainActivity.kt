package org.openproject.camera


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import io.ktor.util.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.openproject.camera.consts.ConstVar
import org.openproject.camera.implementation.GlobalSettings
import org.openproject.camera.implementation.ImageStorage
import org.openproject.camera.implementation.LuminosityAnalyzer
import org.openproject.camera.implementation.Server
import org.openproject.camera.permission.isAcceptCamera
import org.openproject.camera.permission.requestCameraPermission
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


open class MainActivity: AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private val data = ConstVar()
    private lateinit var previews: PreviewView
    private lateinit var server: Server
    private  val  imageStorage: ImageStorage = ImageStorage()
    private lateinit var  imageButton: ImageButton
    private lateinit var cameraButton: Button


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
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previews.surfaceProvider)
                }
            imageCapture = ImageCapture
                    .Builder()
                    .build()
            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer {luma ->
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


    @KtorExperimentalAPI
    override  fun onCreate(savedInstanceState: Bundle?) {
        if (isAcceptCamera(this@MainActivity)) requestCameraPermission(this@MainActivity)
        super.onCreate(savedInstanceState)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        if (!isAcceptCamera(this@MainActivity)) startCamera()
        if (GlobalSettings.startServer && !GlobalSettings.isServerStart){
            server = Server(GlobalSettings.trigger,GlobalSettings.ip,GlobalSettings.port,data.logTag){
                imageCapture?.takePicture(ImageCapture.OutputFileOptions.Builder(imageStorage).build(), ContextCompat.getMainExecutor(this),object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Log.e(data.logTag, "Photo capture failed: ${exc.message}", exc)
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        /*val savedUri = Uri.fromFile(photoFile)
                        val msg = "Photo capture succeeded: $savedUri"
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        Log.d(data.logTag, msg)*/
                    }
                })
                return@Server 1.toByte()
            }
            GlobalScope.launch {
                server.start()
            }
            GlobalSettings.isServerStart = true
        }
        cameraButton = findViewById<Button>(R.id.MakePhoto)
        imageButton = findViewById<ImageButton>(R.id.settings_button)
        previews = findViewById(R.id.viewFinder)
        cameraButton.setOnClickListener{
            this.takePhoto()
        }
        imageButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    @KtorExperimentalAPI
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        if (!GlobalSettings.isServerStart) {
            GlobalScope.launch {
                server.close()
            }
            GlobalSettings.isServerStart = false
        }
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
}