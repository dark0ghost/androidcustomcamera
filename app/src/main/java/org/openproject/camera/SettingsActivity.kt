package org.openproject.camera

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity


class SettingsActivity: AppCompatActivity() {

    private lateinit var changeModeSavePhotoButton: ToggleButton
    private lateinit var changeViewButton: ImageButton




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_layout)
        //changeModeSavePhotoButton = findViewById(R.id.ram_mode)
        changeViewButton = findViewById(R.id.change_view)
        /*changeModeSavePhotoButton.setOnClickListener {
            println("change mode")
        }*/
        changeViewButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        supportActionBar?.hide()
        setListVew()
    }

    /**
            <ToggleButton
        android:id="@+id/ram_mode"
        android:layout_width="70dp"
        android:layout_height="77dp" />
     **/
    private fun setListVew(){
        val listView: ListView = findViewById(R.id.listView)
        val settingsItem: Array<out String> = resources.getStringArray(R.array.ru_text_settings)
        val adapter:ArrayAdapter<String> = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, settingsItem)
        listView.adapter = adapter
    }
}