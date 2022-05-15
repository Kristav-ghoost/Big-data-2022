package com.example.npoproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.npoproject.databinding.ActivityDashboardBinding
import com.example.npoproject.databinding.ActivityLoginBinding
import android.content.Intent
import android.widget.Toast
import com.example.lib.Location
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.gson.Gson
import timber.log.Timber
import java.lang.Exception

class Dashboard : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    lateinit var app: MyApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as MyApplication

        binding.id.setText(app.returnId())
        binding.username.setText(app.returnUsername())
        binding.emailUser.setText(app.returnEmail())
        binding.vUser.setText(app.returnV())
    }

    fun Odjava(view: android.view.View) {
        app.saveLogin(false)
        app.saveUsername("")
        app.saveID("")
        app.saveEmail("")
        app.saveV("")
        val i = Intent(this, Login::class.java)
        startActivity(i)
    }

    fun Send_array(view: android.view.View) {
        val gson = Gson()/*
        val Location_array: Array<Location> = arrayOf(
            Location(123,123),
            Location(321,321)
        );*/
        val Location_array: MutableList<Location> = ArrayList()
        Location_array.add(Location(123.1,123.1))

        val jsonArray: String = gson.toJson(Location_array)
        val author: String? = app.returnId()

        try {
            val fuel = Fuel.post("http://164.8.216.130:777/data/createPhone").jsonBody("{ \"data\" : $jsonArray, \"author\" : \"$author\" }").response { request, response, result -> }
            Timber.d(fuel.get().toString())
            val a = fuel.get()
            val status_code: Int = a.statusCode
            if (status_code != 200){
                Toast.makeText(applicationContext, "Tezava", Toast.LENGTH_SHORT).show()
            }
        }
        catch (e: Exception){
            Timber.d(e.message)
        }
    }
}