package com.example.bastionmobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bastionmobile.databinding.ActivitySelfInformationBinding
import com.example.bastionmobile.model.UserInfo
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.IOException
import java.util.Locale
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class SelfInformationActivity : BaseAfterLoginActivity(), LanguageSelectionDialogFragment.OnLanguageSelectedListener {
    private val shiftEndpoint = "https://192.168.56.1:8080/mobile"
    private lateinit var binding: ActivitySelfInformationBinding
    private val client = createOkHttpClient()
    private val preferences by lazy { AppPreferences(this) }
    private val gson = Gson()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelfInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (preferences.getUserInfo() != null){
            currentPhoneNumber = preferences.getUserInfo()?.phoneNumber
            currentPassword = preferences.getUserInfo()?.password
        }
        else {
            currentPhoneNumber = intent.getStringExtra("PHONE_NUMBER")
            currentPassword = intent.getStringExtra("PASSWORD")
        }
        loadUserInfo()

        val bottomNav = binding.bottomNavigation.bottomNavigation
        setupBottomNavigationView(bottomNav)

        if (currentPhoneNumber == null || currentPassword == null) {
            Toast.makeText(
                this,
                getString(R.string.toast_authentication_error),
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        binding.changeLanguageButton.setOnClickListener {
            LanguageSelectionDialogFragment
                .newInstance()
                .show(supportFragmentManager,
                    LanguageSelectionDialogFragment.TAG)
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun loadUserInfo() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = shiftEndpoint + "/self?phoneNumber=${currentPhoneNumber}"


                val credentials = "$currentPhoneNumber:$currentPassword"
                val authHeader = "Basic " + Base64.Default.encode(credentials.toByteArray())

                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", authHeader)
                    .get()
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("SelfInfo", "Response: $responseBody")

                    if (!responseBody.isNullOrEmpty()) {
                        val userInfo = gson.fromJson(responseBody, UserInfo::class.java)
                        preferences.saveUserInfo(userInfo)

                        withContext(Dispatchers.Main) {
                            displayUserInfo(userInfo)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@SelfInformationActivity, getString(R.string.toast_empty_server_response), Toast.LENGTH_SHORT).show() // Replaced "Порожня відповідь від сервера."
                            binding.userInfoTextView.text = getString(R.string.text_info_not_loaded_empty_response) // Replaced "Інформація не завантажена: порожня відповідь."
                        }
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SelfInformationActivity, getString(R.string.toast_server_error, response.code), Toast.LENGTH_SHORT).show()
                        binding.userInfoTextView.text = getString(R.string.text_info_not_loaded_server_error, response.code, response.message)
                        loadUserInfoFromCache()
                    }
                }
            } catch (e: IOException) {
                Log.e("SelfInfo", getString(R.string.log_self_info_network_error, e.message), e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SelfInformationActivity, getString(R.string.toast_network_error, e.message), Toast.LENGTH_LONG).show()
                    binding.userInfoTextView.text = getString(R.string.text_network_error_loading_cache)
                    loadUserInfoFromCache()
                }
            } catch (e: Exception) {
                Log.e("SelfInfo", getString(R.string.log_self_info_unexpected_error, e.message), e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SelfInformationActivity, getString(R.string.toast_unexpected_error, e.message), Toast.LENGTH_LONG).show()
                    binding.userInfoTextView.text = getString(R.string.text_unexpected_error_loading_cache)
                    loadUserInfoFromCache()
                }
            }
        }
    }

    private fun displayUserInfo(userInfo: UserInfo) {
        val infoText = StringBuilder().apply {
            append(getString(R.string.user_info_name, userInfo.firstName) + "\n")
            append(getString(R.string.user_info_last_name, userInfo.lastName) + "\n")
            append(getString(R.string.user_info_phone, userInfo.phoneNumber) + "\n")
            append(getString(R.string.user_info_role, userInfo.role) + "\n")
            append(getString(R.string.user_info_position, userInfo.position) + "\n")
            append(getString(R.string.user_info_location, userInfo.location?.toString() ?: getString(R.string.user_info_location_not_specified)) + "\n") // Replaced "Не вказано"
            if (userInfo.workTime != null) {
                append(getString(R.string.user_info_work_days, userInfo.workTime.workDays.joinToString()) + "\n")
                append(getString(R.string.user_info_shift_start, userInfo.workTime.shiftStart) + "\n")
                append(getString(R.string.user_info_shift_finish, userInfo.workTime.shiftFinish) + "\n")
            } else {
                append(getString(R.string.user_info_work_time_not_specified) + "\n")
            }
            append(getString(R.string.user_info_on_duty, if (userInfo.onDuty) getString(R.string.user_info_on_duty_yes) else getString(R.string.user_info_on_duty_no)) + "\n") // Replaced "Так" and "Ні"
            append(getString(R.string.user_info_full_name, userInfo.fullName) + "\n")
            append(getString(R.string.user_info_application_role, userInfo.applicationRole) + "\n")
        }.toString()

        binding.userInfoTextView.text = infoText
    }

    private fun loadUserInfoFromCache() {
        val cachedUserInfo = preferences.getUserInfo()
        if (cachedUserInfo != null) {
            displayUserInfo(cachedUserInfo)
            Toast.makeText(this, getString(R.string.toast_info_loaded_from_cache), Toast.LENGTH_SHORT).show()
        } else {
            binding.userInfoTextView.text = getString(R.string.text_info_not_loaded_no_cache)
            Toast.makeText(this, getString(R.string.toast_info_not_in_cache), Toast.LENGTH_SHORT).show()
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