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

    //username
    fun saveUsername(usr: String){
        with(sharedPref.edit()){
            putString("Username", usr)
            apply()
        }
    }

    fun containsUsername(): Boolean{
        return sharedPref.contains("Username")
    }

    fun returnUsername(): String?{
        return sharedPref.getString("Username","DefaultNoData")
    }

    //id
    fun saveID(id: String){
        with(sharedPref.edit()){
            putString("ID", id)
            apply()
        }
    }

    fun containsId(): Boolean{
        return sharedPref.contains("ID")
    }

    fun returnId(): String?{
        return sharedPref.getString("ID","DefaultNoData")
    }

    //email
    fun saveEmail(email: String){
        with(sharedPref.edit()){
            putString("Email", email)
            apply()
        }
    }

    fun containsEmail(): Boolean{
        return sharedPref.contains("Email")
    }

    fun returnEmail(): String?{
        return sharedPref.getString("Email","DefaultNoData")
    }

    //v
    fun saveV(v: String){
        with(sharedPref.edit()){
            putString("V", v)
            apply()
        }
    }

    fun containsV(): Boolean{
        return sharedPref.contains("V")
    }

    fun returnV(): String?{
        return sharedPref.getString("V","DefaultNoData")
    }

    //logedIn
    fun saveLogin(vib: Boolean){
        with(sharedPref.edit()){
            putBoolean("Login", vib)
            apply()
        }
    }

    fun returnLogin(): Boolean{
        return sharedPref.contains("Login")
    }

    fun getLogin(): Boolean{
        return sharedPref.getBoolean("Login",true)
    }
}