package com.example.telesignal.data.login

import android.util.Log
import com.example.telesignal.data.login.dto.LoginDto
import com.example.telesignal.data.login.dto.RegisterDto
import com.example.telesignal.data.login.model.AuthToken
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*

class LoginRepository() {

    private val client = HttpClient(CIO) {
        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            gson()
        }
        expectSuccess = true
    }


    suspend fun makeLoginRequest(loginDto: LoginDto): AuthToken {
        val urlBuilder = URLBuilder(BACKEND_HOST)
        urlBuilder.path(LOGIN_PATH)
        Log.d("URL", urlBuilder.build().toString())
        val response: HttpResponse = client.post {
            url(urlBuilder.build())
            contentType(ContentType.Application.Json)
            setBody(loginDto)
        }
        return response.body()
    }

    suspend fun makeRegisterRequest(registerDto: RegisterDto): Unit {
        val urlBuilder = URLBuilder(BACKEND_HOST)
        urlBuilder.path(REGISTER_PATH)
        val response: HttpResponse = client.post {
            url(urlBuilder.build())
            contentType(ContentType.Application.Json)
            setBody(registerDto)
        }
    }

    companion object Values {

        const val BACKEND_HOST = "http://10.0.2.2:5022" // loopback to 127.0.0.1
        const val LOGIN_PATH = "/auth/login"
        const val REGISTER_PATH = "/auth/register"
    }


}