package com.example.npoproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.npoproject.databinding.ActivityRegisterBinding
import com.example.npoproject.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding
    private val TAG = StartActivity::class.java.simpleName
    lateinit var app: MyApplication



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as MyApplication

    }

    fun Start_tracikg(view: android.view.View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun Account(view: android.view.View) {
        val intent = Intent(this, Dashboard::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        app.data.list.clear()
        app.save()
    }
}