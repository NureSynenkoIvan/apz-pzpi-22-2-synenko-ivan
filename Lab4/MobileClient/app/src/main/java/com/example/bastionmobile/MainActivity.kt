package com.example.bastionmobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.bastionmobile.databinding.ActivityMainBinding
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.Locale

class MainActivity : AppCompatActivity(), LanguageSelectionDialogFragment.OnLanguageSelectedListener {
    private var endpoint = "https://192.168.56.1:8080/shift"
    private lateinit var binding: ActivityMainBinding
    private lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startMainUi()
        appPreferences = AppPreferences(this)

        binding.button2.setOnClickListener {
            initiateQRScan()
        }

        binding.changeLanguageButton.setOnClickListener {
            LanguageSelectionDialogFragment
                .newInstance()
                .show(supportFragmentManager,
                    LanguageSelectionDialogFragment.TAG)
        }

    }



    private fun initiateQRScan() {
        val integrator = IntentIntegrator(this)
        integrator.setPrompt(getString(R.string.scan_qr_prompt))
        integrator.setBeepEnabled(true)
        integrator.setOrientationLocked(false)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            // val qrContent = result.contents
            val qrContent = "7eb6a28a-17c4-445f-afaf-96c2ede762a0"

            CoroutineScope(Dispatchers.IO).launch {
                val isValid = isQRValid(qrContent)
                launch(Dispatchers.Main) {
                    if (isValid) {
                        Toast.makeText(this@MainActivity, getString(R.string.toast_qr_valid), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, getString(R.string.toast_qr_invalid), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_scan_cancelled), Toast.LENGTH_SHORT).show()
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun startMainUi() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private suspend fun isQRValid(content: String): Boolean {
        val client = createOkHttpClient()

        val url = endpoint + "?key=${content}"

        val mediaType = "application/x-www-form-urlencoded".toMediaTypeOrNull()
        val emptyBody = "".toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(url)
            .post(emptyBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("QR_Validation", getString(R.string.log_qr_validation_response, responseBody))
                responseBody?.toBooleanStrictOrNull() ?: false
            } else {
                Log.e("QR_Validation", getString(R.string.log_qr_validation_server_error, response.code.toString(), response.message))
                false
            }
        } catch (e: IOException) {
            Log.e("QR_Validation", getString(R.string.log_qr_validation_network_error, e.message), e)
            false
        }
    }

    override fun onLanguageSelected(languageCode: String) {
        appPreferences.saveLanguage(languageCode)

        setAppLocale(languageCode)

        recreate()
    }

    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
    }
}