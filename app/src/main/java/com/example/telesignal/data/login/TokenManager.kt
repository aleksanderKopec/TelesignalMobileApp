package com.example.telesignal.data.login

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.telesignal.data.login.model.AuthToken
import com.google.gson.Gson
import java.util.*

class AuthTokenManager(activity: Activity) {

    private val sharedPreferences =
        activity.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)
    private val sharedPreferencesEditor = sharedPreferences.edit()
    private val tokenDecoder = Base64.getUrlDecoder()
    private val gson = Gson()

    fun getToken(): AuthToken? {
        Log.d(LOG_TAG, "Shared preferences: ${sharedPreferences.all}")
        return sharedPreferences.getString(AUTH_TOKEN_PREFERENCES_KEY, null)?.let {
            Log.d(LOG_TAG, "Getting token: $it")
            return AuthToken(it)
        }
    }

    fun setToken(token: AuthToken) {
        Log.d(LOG_TAG, "Setting token: ${token.token}")
        sharedPreferencesEditor.putString(AUTH_TOKEN_PREFERENCES_KEY, token.token)
        sharedPreferencesEditor.apply()
        Log.d(LOG_TAG, "Edited shared preferences: ${sharedPreferences.all}")
    }

    fun setToken(token: String) {
        sharedPreferencesEditor.putString(AUTH_TOKEN_PREFERENCES_KEY, token)
        sharedPreferencesEditor.apply()
    }

    fun deleteToken() {
        sharedPreferencesEditor.remove(AUTH_TOKEN_PREFERENCES_KEY)
        sharedPreferencesEditor.apply()
    }

    fun getUsername(): String? {
        Log.d(LOG_TAG, "Getting username")
        return getToken()?.let { extractUsername(it.token) }
    }

    // decodes the token to extract current user
    private fun extractUsername(token: String): String {
        val chunks = token.split(".")
        val payload = String(tokenDecoder.decode(chunks[1]))
        val payloadJson = gson.fromJson<Map<String, Any>>(payload, Map::class.java)
        return payloadJson["sub"] as String
    }

    companion object {

        const val SHARED_PREFERENCES_FILE = "TOKEN_STORE"
        const val AUTH_TOKEN_PREFERENCES_KEY = "AUTH_TOKEN"
        const val LOG_TAG = "AuthTokenManager"
    }
}