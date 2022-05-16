package com.example.npoproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.npoproject.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    lateinit var app: MyApplication


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as MyApplication

        if (app.getLogin() == true){
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }
        else{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}