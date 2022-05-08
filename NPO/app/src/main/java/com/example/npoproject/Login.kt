package com.example.npoproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.npoproject.databinding.ActivityLoginBinding
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import timber.log.Timber
import java.lang.Exception

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.registerBtn.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }

    fun Login(view: android.view.View) {
        val username = binding.username.text
        val password = binding.password.text

        try {
            val fuel = Fuel.post("http://164.8.216.130:777/users/login").jsonBody("{ \"username\" : \"$username\", \"password\" : \"$password\" }").response { request, response, result -> }
            val bol1: Boolean = fuel.get().toString().contains("OK")
            val bol2: Boolean = fuel.get().toString().contains("Unauthorized")

            if (bol1 == true && bol2 == false){
                hideKeyboard()
                binding.username.setText("")
                binding.password.setText("")
                Toast.makeText(applicationContext, "Uspesna prijava!", Toast.LENGTH_SHORT).show()
            }
            else if (bol1 == false && bol2 == true){
                hideKeyboard()
                Toast.makeText(applicationContext, "Tezava z prijavo", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception){
            Toast.makeText(applicationContext, "Tezava z projavo", Toast.LENGTH_SHORT).show()
        }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


}