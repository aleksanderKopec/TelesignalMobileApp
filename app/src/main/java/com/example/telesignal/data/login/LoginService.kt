package com.example.telesignal.data.login

import android.content.Context
import android.content.SharedPreferences
import com.example.telesignal.R
import com.example.telesignal.data.login.dto.LoginDto
import com.example.telesignal.data.login.dto.RegisterDto
import com.example.telesignal.data.login.model.AuthToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class LoginService(private val authTokenManager: AuthTokenManager) {

    private val loginRepository = LoginRepository()


    suspend fun login(loginDto: LoginDto){
        val token = loginRepository.makeLoginRequest(loginDto)
        authTokenManager.setToken(token);
    }

    suspend fun register(registerDto: RegisterDto) {
        val token = loginRepository.makeRegisterRequest(registerDto)
        authTokenManager.setToken(token)
    }

    fun logout() {
        authTokenManager.deleteToken()
    }
}