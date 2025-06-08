package com.example.bastionmobile


import android.content.Context
import android.content.SharedPreferences
import com.example.bastionmobile.model.UserInfo
import com.google.gson.Gson

class AppPreferences(context: Context) {
    private val PREF_NAME = "BastionMobilePrefs"
    private val USER_INFO_KEY = "user_info"
    private val LANGUAGE_KEY = "chosen_language"
    private val UNHASHED_PASSWORD_KEY = "unhashed_password"

    private val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveUserInfo(userInfo: UserInfo) {
        val json = gson.toJson(userInfo)
        preferences.edit().putString(USER_INFO_KEY, json).apply()
    }

    fun getUserInfo(): UserInfo? {
        val json = preferences.getString(USER_INFO_KEY, null)
        return gson.fromJson(json, UserInfo::class.java)
    }

    fun clearUserInfo() {
        preferences.edit().remove(USER_INFO_KEY).apply()
    }

    fun saveLanguage(languageCode: String) {
        preferences.edit().putString(LANGUAGE_KEY, languageCode).apply()
    }


    fun getLanguage(defaultValue: String = "en"): String { // You can set a default language here
        return preferences.getString(LANGUAGE_KEY, defaultValue) ?: defaultValue
    }


    fun clearLanguage() {
        preferences.edit().remove(LANGUAGE_KEY).apply()
    }

    fun getUnhashedPassword(): String {
        return preferences.getString(UNHASHED_PASSWORD_KEY, "").toString()
    }

    fun setUnhashedPassword(key: String){
        preferences.edit().putString(UNHASHED_PASSWORD_KEY, key).apply()
    }
}