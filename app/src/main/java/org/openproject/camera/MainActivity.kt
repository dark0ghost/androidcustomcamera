package org.openproject.camera


import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.Camera
import android.hardware.Camera.Size
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity(),SurfaceHolder.Callback, View.OnClickListener, Camera.PictureCallback, Camera.PreviewCallback, Camera.AutoFocusCallback {
    private  var camera: Camera? = null
    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var preview: SurfaceView
    private lateinit var shotBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        if (this.isAcceptCamera())   ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.CAMERA),1)
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND

        // если хотим, чтобы приложение было полноэкранным
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // и без заголовка
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        preview = findViewById<View>(R.id.SurfaceView) as SurfaceView
        surfaceHolder = preview.holder
        surfaceHolder.addCallback(this)
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        shotBtn = findViewById<View>(R.id.ButtonOne) as Button
        shotBtn.text = "Shot"
        shotBtn.setOnClickListener(this)

    }

    override fun onResume(): Unit {
        super.onResume()

        camera = Camera.open()
    }

    override fun onPause(): Unit {
        super.onPause()
            camera?.setPreviewCallback(null);
            camera?.stopPreview();
            camera?.release();
            camera = if (camera != null) null else camera
        return

    }

   override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int): Unit {

   }

   override fun surfaceCreated(holder: SurfaceHolder): Unit {
       if (camera == null) return
        try {
            camera?.setPreviewDisplay(holder)
            camera?.setPreviewCallback(this)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val previewSize: Size? = camera!!.getParameters().getPreviewSize()
        val aspect: Float = (previewSize?.width as Float) / previewSize.height
        val previewSurfaceWidth = preview.width
        val previewSurfaceHeight = preview.height
        val lp: LayoutParams = preview.layoutParams

        // здесь корректируем размер отображаемого preview, чтобы не было искажений
        if (this.resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            // портретный вид
            camera!!.setDisplayOrientation(90)
            lp.height = previewSurfaceHeight
            lp.width = (previewSurfaceHeight / aspect).toInt()
        } else {
            // ландшафтный
            camera!!.setDisplayOrientation(0)
            lp.width = previewSurfaceWidth
            lp.height = (previewSurfaceWidth / aspect).toInt()
        }
        preview.layoutParams = lp
        camera!!.startPreview()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder): Unit {

    }

    override fun onClick(v: View): Unit {
        if (v === shotBtn) {
            // либо делаем снимок непосредственно здесь
            // 	либо включаем обработчик автофокуса

            //camera.takePicture(null, null, null, this);
            camera?.autoFocus(this)
        }
    }



    override fun onAutoFocus(paramBoolean: Boolean, paramCamera: Camera): Unit {
        if (paramBoolean) {
            // если удалось сфокусироваться, делаем снимок
            paramCamera.takePicture(null, null, null, this)
        }
    }

    override fun onPreviewFrame(paramArrayOfByte: ByteArray, paramCamera: Camera): Unit {
        // здесь можно обрабатывать изображение, показываемое в preview
    }


    override fun  onPictureTaken(paramArrayOfByte: ByteArray, paramCamera: Camera) {
        // сохраняем полученные jpg в папке /sdcard/CameraExample/
        // имя файла - System.currentTimeMillis()

        // сохраняем полученные jpg в папке /sdcard/CameraExample/
        // имя файла - System.currentTimeMillis()
        try {
            val saveDir = File("/sdcard/CameraExample/")
            if (!saveDir.exists()) {
                saveDir.mkdirs()
            }
            val os = FileOutputStream(
                String.format(
                    "/sdcard/CameraExample/%d.jpg",
                    System.currentTimeMillis()
                )
            )
            os.write(paramArrayOfByte)
            os.close()
        } catch (e: Exception) {
        }

        // после того, как снимок сделан, показ превью отключается. необходимо включить его

        // после того, как снимок сделан, показ превью отключается. необходимо включить его
        paramCamera.startPreview()
    }

    internal open class SaveInBackground : AsyncTask<ByteArray, String, String>() {

      override fun doInBackground(vararg arrayOfByte: ByteArray): String? {
            try {
                val saveDir = File("/sdcard/CameraExample/")
                if (!saveDir.exists()) {
                    saveDir.mkdirs()
                }
                val os = FileOutputStream(
                    String.format(
                        "/sdcard/CameraExample/%d.jpg",
                        System.currentTimeMillis()
                    )
                )
                os.write(arrayOfByte[0])
                os.close()
            } catch (e: java.lang.Exception) {
                //
            }
            return null
        }
    }

    private fun isAcceptCamera(): Boolean = checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
}