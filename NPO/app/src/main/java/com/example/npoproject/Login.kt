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
import com.github.kittinunf.fuel.gson.jsonBody
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.http.Body
import timber.log.Timber
import java.lang.Exception

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    lateinit var app: MyApplication


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as MyApplication


        binding.registerBtn.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }

    fun Login(view: android.view.View) {
        val gson = GsonBuilder().setPrettyPrinting().create()

        if (binding.username.text.isNotEmpty() && binding.password.text.isNotEmpty()){
            val username = binding.username.text
            val password = binding.password.text


            try {
                val fuel = Fuel.post("http://164.8.216.130:777/users/login").jsonBody("{ \"username\" : \"$username\", \"password\" : \"$password\" }").response { request, response, result -> }

                val a = fuel.get()
                val status_code = a.statusCode
                Timber.d("$status_code")
                val myBody = String(a.data)
                val parseString = JsonParser.parseString(myBody)
                val jsonObject = gson.fromJson(parseString, JsonObject::class.java)


                if (status_code == 200){
                    hideKeyboard()
                    binding.username.setText("")
                    binding.password.setText("")
                    Toast.makeText(applicationContext, "Uspesna prijava!", Toast.LENGTH_SHORT).show()
                    val id_post = jsonObject.get("_id").asString
                    val username_post = jsonObject.get("username").asString
                    val email_post = jsonObject.get("email").asString
                    val v_post = jsonObject.get("__v").asString
                    app.saveLogin(true)
                    app.saveUsername(username_post)
                    app.saveID(id_post)
                    app.saveEmail(email_post)
                    app.saveV(v_post)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else if (status_code == 401){
                    hideKeyboard()
                    Toast.makeText(applicationContext, "Napacno uporabnisko ime ali geslo", Toast.LENGTH_SHORT).show()
                }
                else{
                    hideKeyboard()
                    Toast.makeText(applicationContext, "Tezava z prijavo", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception){
                Toast.makeText(applicationContext, "Napacno uporabnisko ime ali geslo", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(applicationContext, "Izpolnite vsa polja", Toast.LENGTH_SHORT).show()
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