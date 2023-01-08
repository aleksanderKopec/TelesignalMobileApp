package com.example.telesignal.data.login

import com.example.telesignal.data.login.dto.LoginDto
import com.example.telesignal.data.login.dto.RegisterDto

class LoginService(private val authTokenManager: AuthTokenManager) {

    private val loginRepository = LoginRepository()


    suspend fun login(loginDto: LoginDto) {
        val token = loginRepository.makeLoginRequest(loginDto)
        authTokenManager.setToken(token);
        authTokenManager.setUserId(token.userId)
    }

    suspend fun register(registerDto: RegisterDto) {
        loginRepository.makeRegisterRequest(registerDto)
    }

    fun logout() {
        authTokenManager.deleteToken()
    }
}