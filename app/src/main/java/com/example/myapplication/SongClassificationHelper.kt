package com.example.myapplication

import android.content.Context


class SongClassificationHelper (
    private val context : Context,
    private val options : Options = Options(),

) {
    // Options for running the model. Pull from here to feed to server if necessary
    class Options(
        var location : String = DEFAULT_LOCATION
    )

    companion object {
        const val DEFAULT_LOCATION = " " //Placeholder
    }


    suspend fun classifyWithServer() {

    }

    data class ClassificationResult(
        val categories: List<Category>, val inferenceTime: Long
    )

    data class Category(val label: String, val score: Float)
}