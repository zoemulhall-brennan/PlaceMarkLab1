package com.example.mad_placemarklab1

import com.example.mad_placemarklab1.models.PlacemarkMemStore
import java.io.File
import android.content.Context
object AppData {
    lateinit var placedMarks: PlacemarkMemStore

    fun initialize(context: Context) {
        val file = File(
            context.filesDir,
            "place_list.json"
        )

        placedMarks = PlacemarkMemStore(file)
    }

}