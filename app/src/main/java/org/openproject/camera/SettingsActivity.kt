package org.openproject.camera

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ImageButton
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ButtonBarLayout


class SettingsActivity: AppCompatActivity() {

    private lateinit var changeModeSavePhotoButton: ToggleButton
    private lateinit var changeViewButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_layout)
        changeModeSavePhotoButton = findViewById(R.id.ram_mode)
        changeViewButton = findViewById(R.id.change_view)
        changeModeSavePhotoButton.setOnClickListener {
            println("change mode")
        }
        changeViewButton.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        supportActionBar?.hide()
    }
}