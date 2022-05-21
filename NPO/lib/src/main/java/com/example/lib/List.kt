package com.example.lib

class List( var ime: String) {
    val list: MutableList<MyLocation> = mutableListOf()

    override fun toString(): String {
        return "Ime: $ime"
    }
}