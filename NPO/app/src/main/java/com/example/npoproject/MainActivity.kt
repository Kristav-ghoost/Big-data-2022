package com.example.npoproject

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.telecom.TelecomManager.EXTRA_LOCATION
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.npoproject.databinding.ActivityMainBinding
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.gson.Gson
import timber.log.Timber
import java.lang.Exception
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityMainBinding
    private val TAG = MainActivity::class.java.simpleName
    lateinit var app: MyApplication

    //Sensor
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private var currAccValue: Int = 0
    private var preAccValue: Int = 0

    //Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null

    //Location array
    val locationArray: MutableList<com.example.lib.Location> = ArrayList()
    var latLonSum = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as MyApplication

        //Sensor
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_NORMAL)
        }

        //Location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
            fastestInterval = 1000
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                currentLocation = locationResult.lastLocation
            }
        }

        //Open your profile
        binding.account.setOnClickListener{
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
        }

        getLocation()
    }

    //ACCELEROMETER
    override fun onSensorChanged(p0: SensorEvent?) {
        if(p0?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            val x = p0.values[0]
            val y = p0.values[1]
            val z = p0.values[2]

            currAccValue = sqrt((x * x + y * y + z * z).toDouble()).toInt()
            val diffAcc = Math.abs(currAccValue - preAccValue)
            preAccValue = currAccValue

            // Ce je prislo do tresljaja in ce je lokacija spremenjena dodaj v array
            if(diffAcc > 5){
                getLocation()
                val lat = binding.lat.text.toString().toDouble()
                val lon = binding.lon.text.toString().toDouble()
                if(latLonSum != (lat + lon)){
                    locationArray.add(com.example.lib.Location(lat, lon))
                    Timber.d("Added to arr")
                    latLonSum = lat + lon
                }
            }

            binding.one.setText("Curr = " + currAccValue)
            binding.two.setText("Pre = " + preAccValue)
            binding.three.text = "Diff = " + diffAcc

            binding.progressBar2.progress = diffAcc
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    //LOCATION
    companion object{
        private const val PERMISSION_REQUEST_ACCESS_LOCATION=100
    }

    private fun getLocation(){
        if(checkPermissions()){
            if(isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task->
                    Timber.d(task.result.toString())
                    var location: Location? = task.result
                    if(location == null){
                        Toast.makeText(this, "Error finding location", Toast.LENGTH_SHORT).show()
                    }else{
                        binding.lat.text = location.latitude.toString()
                        binding.lon.text = location.longitude.toString()
                        Toast.makeText(this, location.longitude.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Turn on your location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }else{
            requestPermission()
        }
    }

    private fun isLocationEnabled(): Boolean{
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermissions(): Boolean{
        if(ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                return true
        return false
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_ACCESS_LOCATION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQUEST_ACCESS_LOCATION){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                getLocation()
            }else{
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun locate(view: android.view.View) {
        getLocation()
    }

    fun sendArray(view: android.view.View) {
        val gson = Gson()

        val jsonArray: String = gson.toJson(locationArray)
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














