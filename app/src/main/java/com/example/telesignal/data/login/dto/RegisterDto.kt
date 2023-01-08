package com.example.telesignal.data.login.dto

data class RegisterDto(
        val email: String,
        val username: String,
        val password: String,
        val repeatPassword: String,
        val publicKey: String,
)