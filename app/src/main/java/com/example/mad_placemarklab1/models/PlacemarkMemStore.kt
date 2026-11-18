package com.example.mad_placemarklab1.models

import java.util.concurrent.atomic.AtomicLong

import java.io.File

import com.google.gson.GsonBuilder

import com.google.gson.reflect.TypeToken
class PlacemarkMemStore(private val file: File = File("placemarks.json")) {
    private val placemarks = ArrayList<PlacedMark>()
    private val lastId = AtomicLong(0L)

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    init {
        load()
    }

    fun findAll(): List<PlacedMark> {
        return placemarks
    }

    fun create(placemark: PlacedMark) {
        placemark.validate()
        placemark.id = lastId.incrementAndGet()
        placemarks.add(placemark)
        save()
    }


    fun update(placemark: PlacedMark): Boolean {
        placemark.validate()

        val foundPlacemark = findOne(placemark.id)
        return if (foundPlacemark != null) {
            foundPlacemark.title = placemark.title
            foundPlacemark.desc = placemark.desc
            foundPlacemark.x = placemark.x
            foundPlacemark.y = placemark.y
            foundPlacemark.imageUri = placemark.imageUri
            save()
            true
        } else {
            false
        }
    }


    fun delete(id: Long): Boolean {
        val foundPlacemark = findOne(id)
        return if (foundPlacemark != null) {
            placemarks.remove(foundPlacemark)
            save()
            true
        } else {
            false
        }
    }

    fun findOne(id: Long): PlacedMark? {
        return placemarks.find { p -> p.id == id }
    }

    private fun load() {
        if (!file.exists()) return

        val type = object : TypeToken<ArrayList<PlacedMark>>() {}.type

        val loaded: ArrayList<PlacedMark> =
            gson.fromJson(file.readText(), type) ?: arrayListOf()

        placemarks.clear()
        placemarks.addAll(loaded)

        lastId.set(placemarks.maxOfOrNull { it.id } ?: 0L)
    }

    private fun save() {
        file.parentFile?.mkdirs()
        file.writeText(gson.toJson(placemarks))
    }

}