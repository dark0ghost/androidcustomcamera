package org.dark0ghost.camera

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.dark0ghost.camera.implementation.GlobalSettings


class SettingsActivity: AppCompatActivity() {

    private lateinit var changeViewButton: ImageButton
    private lateinit var  listView: ListView


    private fun setListVew(){
        val settingsItem: Array<String> = resources.getStringArray(R.array.ru_text_settings)
        val arrayItem: MutableList<String> = mutableListOf()
        if (GlobalSettings.isServerStart){
            arrayItem.add(("включен " + settingsItem[0]))
        }else{
            arrayItem.add(("выключен " + settingsItem[0]))
        }
        if (GlobalSettings.isRangeFinderStart){
            arrayItem.add(("включен " + settingsItem[1]))
        }else{
            arrayItem.add(("выключен " + settingsItem[1]))
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                arrayItem
        )
        listView.adapter = adapter
    }

    private fun changeListView(textChange: String, changePosition: List<Int>): ListAdapter {
        val settingsItem: Array<String> = resources.getStringArray(R.array.ru_text_settings)
        val arrayItem: MutableList<String> = mutableListOf()
        for (i in changePosition)
        arrayItem.add("$textChange ${settingsItem[i]}")
        return ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                arrayItem
        )
    }


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
            println(text)
            if (text == "выключен сервер") {
                GlobalSettings.server.start()
                GlobalSettings.isServerStart = true
                listView.adapter = changeListView("включен", listOf(0))
                return@setOnItemClickListener
            }
            if(text == "включен сервер"){
                GlobalSettings.server.stopServer()
                GlobalSettings.isServerStart = false
                listView.adapter = changeListView("выключен",listOf(0))
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
}