package com.example.bastionmobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bastionmobile.databinding.ActivityLoginBinding
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class LoginActivity : AppCompatActivity() {
    private var loginEndpoint = "https://192.168.56.1:8080/login"
    private var shiftEndpoint = "https://192.168.56.1:8080/mobile"
    private lateinit var binding: ActivityLoginBinding
    private lateinit var appPreferences: AppPreferences
    private val client = createOkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appPreferences = AppPreferences(this)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val phoneNumber = binding.editTextText.text.toString().trim()
        val password = binding.editTextTextPassword.text.toString().trim()

        if (phoneNumber.isEmpty()) {
            binding.editTextText.error = getString(R.string.error_phone_empty)
            return
        }
        if (password.isEmpty()) {
            binding.editTextTextPassword.error = getString(R.string.error_password_empty)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val isCheckCorrect = checkPassword(phoneNumber, password)

            if (isCheckCorrect) {
                val clockInSuccess = clockIn(phoneNumber, password)

                withContext(Dispatchers.Main) {
                    if (clockInSuccess == true) {
                        Toast.makeText(this@LoginActivity, getString(R.string.toast_login_successful_clock_in), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@LoginActivity, getString(R.string.toast_login_successful_no_clock_in), Toast.LENGTH_SHORT).show()
                    }

                    val intent = Intent(this@LoginActivity, SelfInformationActivity::class.java)
                    intent.putExtra("PHONE_NUMBER", phoneNumber)
                    intent.putExtra("PASSWORD", password)

                    appPreferences.setUnhashedPassword(password)
                    startActivity(intent)
                    finish()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, getString(R.string.toast_login_invalid), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun checkPassword(phoneNumber : String, password : String) : Boolean {

        val url = loginEndpoint+"?phoneNumber=${phoneNumber}&password=${password}"

        val emptyBody = "".toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(url)
            .post(emptyBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                var responseBody = response.body?.string()
                if (responseBody.isNullOrEmpty()) {
                    Log.e("Login_Validation", getString(R.string.log_error_empty_response))
                    return false
                }

                try {
                    val jsonResponse = JSONObject(responseBody)
                    val message = jsonResponse.optString("message", "")

                    return message == "Login successful"
                } catch (e: JSONException) {
                    Log.e("Login_Validation", getString(R.string.log_error_json_parsing) +"${e.message}", e)
                    return false
                }
            } else {
                Log.e("Login_Validation", getString(R.string.log_error_network)  +"${response.code} - ${response.message}")
                false
            }
        } catch (e: IOException) {
            Log.e("Login_Validation", getString(R.string.log_error_network) + "${e.message}", e)
            false
        } catch (e: Exception) {
            Log.e("Login_Validation", getString(R.string.log_error_unexpected)+"${e.message}", e)
            false
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private suspend fun clockIn(phoneNumber : String, password : String): Boolean {


        val url = shiftEndpoint+"/on-shift?phoneNumber=${phoneNumber}&fcmToken=${getFCMToken()}"

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
                Log.d("Login_Validation", getString(R.string.toast_login_successful_clock_in)+ responseBody)
                true
            } else {
                Log.e("Login_Validation", getString(R.string.toast_login_successful_no_clock_in) + "${response.code} - ${response.message}")
                false
            }
        } catch (e: IOException) {
            Log.e("Login_Validation", getString(R.string.log_error_network) +{e.message})
            false
        } catch (e: Exception) {
            Log.e("Login_Validation", getString(R.string.log_error_unexpected)+"${e.message}", e)
            false
        }
    }

    private suspend fun getFCMToken(): String? {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            Log.d("FCM_Token", getString(R.string.log_fcm_token_received))
            token
        } catch (e: Exception) {
            Log.e("FCM_Token", getString(R.string.log_fcm_token_received) +e.message, e)
            null
        }
    }
}