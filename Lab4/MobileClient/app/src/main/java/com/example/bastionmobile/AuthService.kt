package com.example.bastionmobile

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import android.util.Log


object AuthService {
    private val client = createOkHttpClient()
    private val shiftEndpoint = "https://192.168.56.1:8080/mobile"

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun clockOut(phoneNumber : String, password : String): Boolean {
        val url = shiftEndpoint+"/off-shift?phoneNumber=${phoneNumber}"

        val credentials = "$phoneNumber:$password"
        val authHeader = "Basic " + Base64.Default.encode(credentials.toByteArray())

        val emptyBody = "".toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .header("Authorization", authHeader)
            .put(emptyBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("SelfInfo", "Clock-out successful. Response: $responseBody")
                true
            } else {
                Log.e("SelfInfo", "Server returned an error for clock-out: ${response.code} - ${response.message}")
                false
            }
        } catch (e: IOException) {
            Log.e("SelfInfo", "Network error during clock-out: ${e.message}", e)
            false
        } catch (e: Exception) {
            Log.e("SelfInfo", "An unexpected error occurred during clock-out: ${e.message}", e)
            false
        }
    }
}