package com.example.myapplication

import android.util.Log
import okhttp3.*
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

object ServerHelper {
    private const val SERVER_URL = "http://prectriv.cc:5000/analyze"
    private val client = OkHttpClient()

    fun sendAudioFile(
        audioFilePath: String,
        lat: Double,
        lon: Double,
        date: Map<String, Int>,
        minConf: Double,
        callback: (String?, Exception?) -> Unit
    ) {
        val file = File(audioFilePath)
        if (!file.exists()) {
            callback(null, IOException("Audio file not found"))
            return
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("audio_file", file.name, file.asRequestBody())
            .addFormDataPart("lat", lat.toString())
            .addFormDataPart("lon", lon.toString())
            .addFormDataPart("date", date.toString())
            .addFormDataPart("min_conf", minConf.toString())
            .build()

        val request = Request.Builder()
            .url(SERVER_URL)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("ServerHelper", "Server response: $responseBody") // Log server response
                    callback(responseBody, null)
                } else {
                    Log.d("ServerHelper", "Server error: ${response.code} \n ${response.body?.string()}") // Log server error
                    callback(null, IOException("Server error: ${response.code}"))
                }
            }
        })
    }
}
