package org.dark0ghost.camera

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.dark0ghost.camera.implementation.GlobalSettings


class SettingsActivity: AppCompatActivity() {

    private lateinit var changeViewButton: ImageButton
    private lateinit var  listView: ListView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_layout)
        changeViewButton = findViewById(R.id.change_view)
        listView = findViewById(R.id.listView)
        setListVew()
        listView.setOnItemClickListener {
            _, view, _, _ ->
            val tView: TextView = view as TextView
            val text = tView.text.toString()
            if (text == "сервер"){
                // TODO: make off/on server and save parameter
                return@setOnItemClickListener
            }
            if(text == "дальномер"){

                return@setOnItemClickListener
            }
        }
        changeViewButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        supportActionBar?.hide()
    }


    private fun setListVew(){
        val settingsItem: Array<String> = resources.getStringArray(R.array.ru_text_settings)
        if (GlobalSettings.isServerStart){
            settingsItem[0] = (("включен " + settingsItem[0]))
        }else{
            settingsItem[0] = (("выключен " + settingsItem[0]))
        }
        if (GlobalSettings.isRangeFinderStart){
            settingsItem[1] = (("включен " + settingsItem[1]))
        }else{
            settingsItem[1] = (("выключен " + settingsItem[1]))
        }
        val adapter:ArrayAdapter<String> = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, settingsItem)
        listView.adapter = adapter
    }
}