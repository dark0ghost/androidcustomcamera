package org.dark0ghost.camera


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat.applyTheme
import androidx.core.graphics.drawable.DrawableCompat.setHotspot
import org.dark0ghost.camera.consts.ConstVar
import org.dark0ghost.camera.fn.setGlobalSettingsFromContext
import org.dark0ghost.camera.fn.setNewPref
import org.dark0ghost.camera.implementation.*
import org.dark0ghost.camera.permission.isAcceptCamera
import org.dark0ghost.camera.permission.requestCameraPermission
import java.io.File
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


open class MainActivity: AppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private val data: ConstVar = ConstVar()
    private lateinit var previews: PreviewView
    private lateinit var server: ThreadServer
    private val imageStorage: ImageStorage = ImageStorage()
    private lateinit var imageButton: ImageButton
    private lateinit var cameraButton: Button
    private lateinit var prefs: SharedPreferences

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
                           // Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                            Log.d(data.logTag, msg)
                        }
                    }
            )
            return
        }

        imageCapture?.takePicture(
                    ImageCapture
                            .OutputFileOptions
                            .Builder(imageStorage)
                            .build(),
                    ContextCompat
                            .getMainExecutor(this),
                    RamCallBack(data
                            .logTag
                    )
            )

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
            } }, ContextCompat.getMainExecutor(this))
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, "camera").apply {
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
        setContentView(R.layout.activity_main)
        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        setGlobalSettingsFromContext(prefs, data)
        supportActionBar?.hide()
        if (!isAcceptCamera(this@MainActivity)) startCamera()
        if (!GlobalSettings.isServerStart) {
            server = ThreadServer(GlobalSettings.trigger, GlobalSettings.port, data.logTag) {
                imageCapture?.takePicture(
                        ImageCapture
                                .OutputFileOptions
                                .Builder(imageStorage)
                                .build(),
                        ContextCompat
                                .getMainExecutor(this),
                        RamCallBack(data
                                .logTag
                        ) {
                            server.isPhotoSave = true
                        }
                )
                while (!server.isPhotoSave) sleep(10)
                val result = this@MainActivity.imageStorage.intArray.toString()
                this@MainActivity.imageStorage.intArray.clear()
                return@ThreadServer result
            }
            GlobalSettings.server = server
        }
        if (GlobalSettings.startServer && !GlobalSettings.isServerStart) {
            server.start()
            GlobalSettings.isServerStart = true
        }
        cameraButton = findViewById(R.id.MakePhoto)
        imageButton = findViewById(R.id.settings_button)
        previews = findViewById(R.id.viewFinder)
        cameraButton.setOnClickListener {
            val shake: Animation = AnimationUtils.loadAnimation(this, R.anim.shake)
            this.takePhoto()
            it.startAnimation(shake)
        }
        imageButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
        Log.d(data.logTag, GlobalSettings.ipServer)
    }

    override fun onDestroy() {
        super.onDestroy()
        setNewPref(prefs, data)
        cameraExecutor.shutdown()
        if (!GlobalSettings.isServerStart) {
            server.close()
        }
        imageStorage.close()
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
        imageStorage.close()
        super.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        setNewPref(prefs, data)
    }
}