package com.example.npoproject

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.lib.List
import com.example.lib.MyLocation
import com.google.gson.Gson
import org.apache.commons.io.FileUtils
import timber.log.Timber
import tomatobean.jsonparser.toJson
import java.io.File
import java.io.IOException

const val shared_preferences = "shared.data"
const val json_data = "mydata.json"

class MyApplication: Application(){
    lateinit var gson: Gson
    lateinit var file: File
    lateinit var sharedPref: SharedPreferences
    lateinit var data: List

    override fun onCreate() {
        Timber.plant(Timber.DebugTree());
        Timber.d("Application created")
        sharedPref = getSharedPreferences(shared_preferences, Context.MODE_PRIVATE)
        gson = Gson()
        file = File(filesDir, json_data)
        Timber.d("File path ${file.path}")
        initData()
        super.onCreate()
    }

    fun return_array(): String?{
        val data = FileUtils.readFileToString(file)
        return data
    }

    fun initData() {
        data = try {
            Timber.d("My file data: ${FileUtils.readFileToString(file)}")
            gson.fromJson(FileUtils.readFileToString(file), List::class.java)
        } catch (e: IOException){
            Timber.d("No file init data")
            List("mylist")
        }
    }

    fun save(){
        try {
            FileUtils.writeStringToFile(file, gson.toJson(data))
            Timber.d("Saved to file " + file.path)
        } catch (e: IOException){
            Timber.d("Can not save to file " + file.path)
        }
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