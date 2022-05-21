package com.example.lib

class MyLocation(private val lat: Double, private val lng: Double, private val pow: Int) {
    override fun toString(): String {
        return "Latitude $lat, longitude $lng, power $pow"
    }
}