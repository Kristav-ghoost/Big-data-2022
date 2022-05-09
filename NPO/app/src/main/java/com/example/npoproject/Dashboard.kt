package com.example.npoproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.npoproject.databinding.ActivityDashboardBinding
import com.example.npoproject.databinding.ActivityLoginBinding
import android.content.Intent




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
}