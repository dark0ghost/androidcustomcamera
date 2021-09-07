package org.dark0ghost.camera

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.dark0ghost.camera.consts.ConstVar
import org.dark0ghost.camera.fn.setNewPref
import org.dark0ghost.camera.settings.GlobalSettings
import org.dark0ghost.camera.enums.State
import org.dark0ghost.camera.fn.printTrace


class SettingsActivity: AppCompatActivity() {

    private lateinit var changeViewButton: ImageButton

    private lateinit var listView: ListView

    private lateinit var prefs: SharedPreferences

    private lateinit var editText: EditText

    private val data = ConstVar()

    private fun getStat(stat: Boolean): String {
        if (stat) return "включен "
        return "выключен "

    }

    private fun setListVew() {
        val settingsItem: Array<String> = resources.getStringArray(R.array.ru_text_settings)
        val arrayItem: MutableList<String> = mutableListOf()
        if (GlobalSettings.isServerStart) {
            arrayItem.add(("включен ${settingsItem[0]}"))
        } else {
            arrayItem.add(("выключен ${settingsItem[0]}"))
        }
        if (GlobalSettings.isRangeFinderStart) {
            arrayItem.add(("включен ${settingsItem[1]}"))
        } else {
            arrayItem.add(("выключен ${settingsItem[1]}"))
        }
        if (GlobalSettings.debugSavePhotoMode) {
            arrayItem.add(("включен ${settingsItem[2]}"))
        } else {
            arrayItem.add(("выключен ${settingsItem[2]}"))
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            arrayItem
        )
        listView.adapter = adapter
    }

    private fun changeListView(
        textChange: String,
        changePosition: List<Int>,
        nextItem: List<String>
    ): ListAdapter {
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
        editText = findViewById(R.id.ip)
        editText.hint = GlobalSettings.ip
        listView.setOnItemClickListener { _, view, _, _ ->
            val tView: TextView = view as TextView
            val text = tView.text.toString()
            Log.d(data.logTag, text)
            if (text == "выключен сервер" && !GlobalSettings.isServerStart) {
                try {
                    GlobalSettings.server.start()
                }catch (e: IllegalThreadStateException){
                    Log.e(data.logTag,"server is start: ${printTrace(e)}")
                }
                GlobalSettings.isServerStart = true
                GlobalSettings.startServer = true
                val nextItem: List<String> =
                    listOf("${getStat(GlobalSettings.isRangeFinderStart)} дальномер","${getStat(GlobalSettings.debugSavePhotoMode)} режим отладки передачи фото")
                listView.adapter = changeListView(State.ON.str, listOf(0), nextItem)
                return@setOnItemClickListener
            }
            if (text == "включен сервер" && GlobalSettings.isServerStart) {
                GlobalSettings.server.stopServer()
                GlobalSettings.startServer = false
                val nextItem: List<String> =
                    listOf("${getStat(GlobalSettings.isRangeFinderStart)} дальномер","${getStat(GlobalSettings.debugSavePhotoMode)} режим отладки передачи фото")
                listView.adapter = changeListView(State.OFF.str, listOf(0), nextItem)
                return@setOnItemClickListener
            }
            if (text == "дальномер") {

                return@setOnItemClickListener
            }
            if (text == "выключен режим отладки передачи фото") {
                GlobalSettings.debugSavePhotoMode = true
                val nextItem: List<String> =
                    listOf("${getStat(GlobalSettings.isRangeFinderStart)} дальномер","${getStat(GlobalSettings.debugSavePhotoMode)} режим отладки передачи фото")
                listView.adapter = changeListView(State.OFF.str, listOf(0), nextItem)
                return@setOnItemClickListener
            }
            if (text == "включен  режим отладки передачи фото") {
                GlobalSettings.debugSavePhotoMode = false
                val nextItem: List<String> =
                    listOf("${getStat(GlobalSettings.isRangeFinderStart)} дальномер","${getStat(GlobalSettings.debugSavePhotoMode)} режим отладки передачи фото")
                listView.adapter = changeListView(State.OFF.str, listOf(0), nextItem)
                return@setOnItemClickListener
            }
        }

        changeViewButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                GlobalSettings.ip = editText.text.toString()
                Log.d("editor", editText.text.toString())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }


        supportActionBar?.hide()
    }

    override fun onPause() {
        super.onPause()
        setNewPref(prefs, data)
    }
}