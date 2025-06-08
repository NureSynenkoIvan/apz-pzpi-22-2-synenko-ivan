package com.example.bastionmobile

import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bastionmobile.AuthService.clockOut
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


open class BaseAfterLoginActivity : AppCompatActivity() {
    protected lateinit var appPreferences: AppPreferences

    protected var currentPhoneNumber: String? = null
    protected var currentPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appPreferences = AppPreferences(this)

        val userInfo = appPreferences.getUserInfo()
        currentPhoneNumber = userInfo?.phoneNumber
        currentPassword = userInfo?.password
    }

    protected fun setupBottomNavigationView(bottomNav: BottomNavigationView) {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_user -> {
                    if (this !is SelfInformationActivity) {
                        startActivity(Intent(this, SelfInformationActivity::class.java))
                        finish()
                    }
                    true
                }

                R.id.navigation_alarms -> {
                    if (this !is AlertInformationActivity) {
                        startActivity(Intent(this, AlertInformationActivity::class.java))
                        finish()
                    }
                    true
                }

                R.id.navigation_logout -> {
                    performLogout()
                    true
                }

                else -> false
            }
        }
    }

    protected fun performLogout() {
        val userInfo = appPreferences.getUserInfo()
        currentPhoneNumber = userInfo?.phoneNumber
        currentPassword = userInfo?.password

        if (currentPhoneNumber == null || currentPassword == null) {
            Toast.makeText(
                this,
                getString(R.string.toast_logout_credentials_missing),
                Toast.LENGTH_LONG
            ).show()
            finalizeLogout()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val clockOutSuccess = try {
                clockOut(currentPhoneNumber!!, appPreferences.getUnhashedPassword())
            } catch (e: Exception) {
                Log.e("BaseActivity", "Clock out failed: ${e.message}", e)
                false
            }

            withContext(Dispatchers.Main) {
                if (clockOutSuccess) {
                    Toast.makeText(
                        this@BaseAfterLoginActivity,
                        getString(R.string.toast_logout_successful),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@BaseAfterLoginActivity,
                        getString(R.string.toast_logout_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }
                finalizeLogout()
            }
        }
    }

    protected fun finalizeLogout() {
        appPreferences.clearUserInfo() // USING AppPreferences
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finishAffinity()
    }
}