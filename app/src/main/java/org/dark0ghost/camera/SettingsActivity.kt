package org.dark0ghost.camera

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.dark0ghost.camera.consts.ConstVar
import org.dark0ghost.camera.fn.setNewPref
import org.dark0ghost.camera.implementation.GlobalSettings
import org.dark0ghost.camera.implementation.State


class SettingsActivity: AppCompatActivity() {

    private lateinit var changeViewButton: ImageButton
    private lateinit var  listView: ListView
    private lateinit var prefs: SharedPreferences
    private val data = ConstVar()

    private fun getStat(stat: Boolean): String {
        if(stat) return "включен "
        return "выключен "

    }
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

    private fun changeListView(textChange: String, changePosition: List<Int>,nextItem: List<String>): ListAdapter {
        val settingsItem: Array<String> = resources.getStringArray(R.array.ru_text_settings)
        val arrayItem: MutableList<String> = mutableListOf()
        for (i in changePosition)
        arrayItem.add("$textChange ${settingsItem[i]}")
        arrayItem.addAll(nextItem)
        return ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                arrayItem
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_layout)
        prefs = getSharedPreferences("settings", MODE_PRIVATE)
        changeViewButton = findViewById(R.id.change_view)
        listView = findViewById(R.id.listView)
        setListVew()
        listView.setOnItemClickListener {
            _, view, _, _ ->
            val tView: TextView = view as TextView
            val text = tView.text.toString()
            println(text)
            if (text == "выключен сервер" && !GlobalSettings.isServerStart) {
                GlobalSettings.server.start()
                GlobalSettings.isServerStart = true
                GlobalSettings.startServer = true
                val nextItem: List<String> = listOf("${getStat(GlobalSettings.isRangeFinderStart)} дальномер")
                listView.adapter = changeListView(State.ON.str, listOf(0),nextItem )
                return@setOnItemClickListener
            }
            if(text == "включен сервер" && GlobalSettings.isServerStart){
                GlobalSettings.server.stopServer()
                GlobalSettings.isServerStart = false
                GlobalSettings.startServer = false
                val nextItem: List<String> = listOf("${getStat(GlobalSettings.isRangeFinderStart)} дальномер")
                listView.adapter = changeListView(State.OFF.str,listOf(0),nextItem)
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
    override fun onPause() {
        super.onPause()
        setNewPref(prefs, data)
    }
}