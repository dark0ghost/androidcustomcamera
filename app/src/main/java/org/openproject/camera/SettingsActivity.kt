package org.openproject.camera

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ButtonBarLayout


class SettingsActivity: AppCompatActivity() {

    private lateinit var changeModeSavePhotoButton: ButtonBarLayout

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_layout)
       // changeModeSavePhotoButton = findViewById(R.id)
        supportActionBar?.hide()
    }

}