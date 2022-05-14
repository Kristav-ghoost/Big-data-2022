package com.example.lib

class Location(val lat: Int, val lng: Int) {
    override fun toString(): String {
        return "Latitude $lat, longitude $lng"
    }
}