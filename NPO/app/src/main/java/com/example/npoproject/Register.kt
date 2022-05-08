package com.example.npoproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.npoproject.databinding.ActivityRegisterBinding
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.google.gson.JsonParseException
import timber.log.Timber
import java.lang.Exception

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val TAG = Register::class.java.simpleName
    lateinit var app: MyApplication




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as MyApplication
    }

    fun Register(view: android.view.View) {
        val username: String = binding.username.text.toString()
        val password: String = binding.password.text.toString()
        val email: String = binding.email.text.toString()

        try {
            val fuel = Fuel.post("http://164.8.216.130:777/users").jsonBody("{ \"username\" : \"$username\", \"password\" : \"$password\", \"email\" : \"$email\" }").response { result -> }
            val bol: Boolean = fuel.get().toString().contains("Created")

            if (bol){
                binding.username.setText("")
                binding.password.setText("")
                binding.email.setText("")
                Toast.makeText(applicationContext, "Uspesna registracija!", Toast.LENGTH_SHORT).show()
                finish()
            }
            else{
                Toast.makeText(applicationContext, "Tezava z registracijo", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception){
            Toast.makeText(applicationContext, "Tezava z registracijo", Toast.LENGTH_SHORT).show()
        }
    }
}