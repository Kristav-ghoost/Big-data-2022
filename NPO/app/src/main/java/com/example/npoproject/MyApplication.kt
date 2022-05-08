package com.example.npoproject

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import timber.log.Timber
import java.io.File
import java.util.*

const val shared_preferences = "shared.data"
const val json_data = "mydata.json"

class MyApplication: Application(){
    lateinit var gson: Gson
    lateinit var file: File
    lateinit var sharedPref: SharedPreferences

    override fun onCreate() {
        Timber.plant(Timber.DebugTree());
        Timber.d("Application created")
        sharedPref = getSharedPreferences(shared_preferences, Context.MODE_PRIVATE)
        gson = Gson()
        file = File(filesDir, json_data)
        Timber.d("File path ${file.path}")
        super.onCreate()
    }
}