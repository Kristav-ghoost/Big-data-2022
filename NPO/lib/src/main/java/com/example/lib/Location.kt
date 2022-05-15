package com.example.lib

class Location(private val lat: Double, private val lng: Double) {
    override fun toString(): String {
        return "Latitude $lat, longitude $lng"
    }
}