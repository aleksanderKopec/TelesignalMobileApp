package com.example.telesignal.ui.login.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Log.WARN
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.telesignal.R
import com.example.telesignal.data.login.AuthTokenManager
import com.example.telesignal.data.login.LoginService
import com.example.telesignal.data.login.dto.LoginDto
import com.example.telesignal.ui.chat.ChatActivity
import com.example.telesignal.ui.login.LoginActivity
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginFragment: Fragment(R.layout.fragment_login) {

    private lateinit var loginService: LoginService

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginService = LoginService(AuthTokenManager(requireActivity()))
        usernameEditText = view.requireViewById(R.id.username)
        passwordEditText = view.requireViewById(R.id.password)
        signInButton = view.requireViewById(R.id.login)

        signInButton.setOnClickListener {
            val loginInfo = LoginDto(usernameEditText.text.toString(), passwordEditText.text.toString())
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    loginService.login(loginInfo)
                    Toast.makeText(context, "Successfully logged in.", Toast.LENGTH_SHORT).show()
                    redirectToChatActivity()
                }
                catch (ex: ClientRequestException) {
                    Log.w(LOG_TAG, "Invalid request response - status code: ${ex.response.status}, body: ${ex.response.bodyAsText()}")
                    Toast.makeText(context, "Failed to log in", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun redirectToChatActivity() {
        val chatIntent = Intent(requireContext(), ChatActivity::class.java)
        startActivity(chatIntent)
        requireActivity().finish()
    }

    companion object {
        const val LOG_TAG = "LoginFragment"
    }
}