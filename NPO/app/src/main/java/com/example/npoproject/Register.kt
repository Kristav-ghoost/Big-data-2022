package com.example.npoproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.npoproject.databinding.ActivityRegisterBinding
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
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

        if (binding.username.text.isNotEmpty() && binding.password.text.isNotEmpty() && binding.email.text.isNotEmpty()){
            val username: String = binding.username.text.toString()
            val password: String = binding.password.text.toString()
            val email: String = binding.email.text.toString()

            try {
                val fuel = Fuel.post("http://192.168.100.30:3000/users").jsonBody("{ \"username\" : \"$username\", \"password\" : \"$password\", \"email\" : \"$email\" }").response { request, response, result -> }
                val a = fuel.get()
                val status_code = a.statusCode
                Timber.d("$status_code")

                if (status_code == 200){
                    binding.username.setText("")
                    binding.password.setText("")
                    binding.email.setText("")
                    Toast.makeText(applicationContext, "Uspesna registracija!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else if (status_code == 500){
                    Toast.makeText(applicationContext, "Uporabnisko ime ali email sta zasedena", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(applicationContext, "Tezava z registracijo", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception){
                Toast.makeText(applicationContext, "Tezava z registracijo", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(applicationContext, "Izpolnite vsa polja", Toast.LENGTH_SHORT).show()
        }
    }
}