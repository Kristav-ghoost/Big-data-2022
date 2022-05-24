package com.example.npoproject

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.npoproject.databinding.ActivityAuthenticateBinding
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception
import java.util.*

private const val FILE_NAME = "photo.jpg"
private const val REQUEST_CODE = 42
private lateinit var photoFile: File
private const val PREFERRED_IMAGE_SIZE = 1000
private const val ONE_MB_TO_KB = 1024
private val baos = ByteArrayOutputStream()
class AuthenticateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticateBinding
    lateinit var app: MyApplication


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as MyApplication
    }


    fun Zajemi(view: android.view.View) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile(FILE_NAME)
        val fileProvider = FileProvider.getUriForFile(this, "com.example.npoproject.fileprovider", photoFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        if (takePictureIntent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE)
        } else {
            Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val takenImage: Bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            val two = Bitmap.createScaledBitmap(takenImage, 900, 1300, false)
            binding.imageView.setImageBitmap(two)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun Send(view: android.view.View) {
        val gson = Gson()
        val takenImage: Bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
        val scaled = Bitmap.createScaledBitmap(takenImage, 600, 900, false)
        scaled.compress(Bitmap.CompressFormat.PNG,100, baos)
        val img64: String = Base64.getEncoder().encodeToString(baos.toByteArray())
        val user = app.returnUsername()
        try {

            val fuel = Fuel.post("http://164.8.216.130:777/users/login_2fa").jsonBody("{ \"data\" : \"$img64\", \"user\" : \"$user\" }").response { request, response, result -> }
            Timber.d(fuel.get().toString())
            val a = fuel.get()
            val myBody = String(a.data)
            val parseString = JsonParser.parseString(myBody)
            val jsonObject = gson.fromJson(parseString, JsonObject::class.java)
            val name = jsonObject.get("name").asString
            //val status_code: Int = a.statusCode
            //if (status_code != 200){
            //    Toast.makeText(applicationContext, "Tezava", Toast.LENGTH_SHORT).show()
            //}
            //else{
                if(name == app.returnUsername()){
                    Toast.makeText(applicationContext, "Welcome $name!", Toast.LENGTH_LONG).show()
                    val i = Intent(this, StartActivity::class.java)
                    startActivity(i)
                    finish()
                }else{
                    Toast.makeText(applicationContext, "Access denied!", Toast.LENGTH_LONG).show()
                    app.saveLogin(false)
                    app.saveUsername("")
                    app.saveID("")
                    app.saveEmail("")
                    app.saveV("")
                    val i = Intent(this, Login::class.java)
                    startActivity(i)
                    finish()
                }
            //}
        }
        catch (e: Exception){
            Timber.d(e.message)
        }

    }


}

