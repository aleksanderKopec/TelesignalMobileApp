package com.example.telesignal.data.login

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.example.telesignal.R
import com.example.telesignal.data.login.model.AuthToken

class AuthTokenManager(activity: Activity) {

    private val sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)

    fun getToken(): AuthToken? {
        return sharedPreferences.getString(AUTH_TOKEN_PREFERENCES_KEY, null)?.let { AuthToken(it) }
    }

    fun setToken(token: AuthToken) {
        sharedPreferences.edit().putString(AUTH_TOKEN_PREFERENCES_KEY, token.token).apply()
    }

    fun setToken(token: String) {
        sharedPreferences.edit().putString(AUTH_TOKEN_PREFERENCES_KEY, token).apply()
    }

    fun deleteToken() {
        sharedPreferences.edit().remove(AUTH_TOKEN_PREFERENCES_KEY).apply()
    }

    companion object {
        const val AUTH_TOKEN_PREFERENCES_KEY = "AUTH_TOKEN"
    }
}