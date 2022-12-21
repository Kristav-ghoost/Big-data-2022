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
import com.example.lib.MyLocation
import com.example.npoproject.databinding.ActivityMainBinding
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONObject
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
    val myLocationArray: MutableList<com.example.lib.MyLocation> = ArrayList()
    var latLonSum = 0.0
    var lat: Double = 0.0
    var lon: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as MyApplication
        app.data.list.clear()
        app.save()

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
            if(diffAcc > 12){
                getLocation()
                if(latLonSum != (lat + lon)){
                    //locationArray.add(com.example.lib.Location(lat, lon))
                    app.data.list.add(MyLocation(lat, lon, diffAcc))
                    app.save()
                    Timber.d("Added to arr")
                    latLonSum = lat + lon
                    Thread.sleep(500)
                }
            }

            //binding.one.setText("Curr = " + currAccValue)
            //binding.two.setText("Pre = " + preAccValue)
            binding.three.text = "Power: " + diffAcc

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
                        lat = location.latitude
                        lon = location.longitude
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

    fun sendArray(view: android.view.View) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val myBody = app.return_array()
        val parseString = JsonParser.parseString(myBody)
        val jsonObject = gson.fromJson(parseString, JsonObject::class.java)
        val mylist = jsonObject.get("list").asJsonArray

        val author: String? = app.returnId()
        app.data.list.clear()
        app.save()

        try {
            val fuel = Fuel.post("http://164.8.220.63:81/data/createPhone").jsonBody("{ \"data\" : $mylist, \"author\" : \"$author\" }").response { request, response, result -> }
            Timber.d(fuel.get().toString())
            val a = fuel.get()
            val status_code: Int = a.statusCode
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
            finish()
        }
        catch (e: Exception){
            Timber.d(e.message)
        }
    }
}














