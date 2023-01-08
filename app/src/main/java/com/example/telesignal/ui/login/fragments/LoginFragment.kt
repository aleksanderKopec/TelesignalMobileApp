package com.example.telesignal.ui.login.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.telesignal.R
import com.example.telesignal.data.cipher.CipherUtils
import com.example.telesignal.data.login.AuthTokenManager
import com.example.telesignal.data.login.LoginService
import com.example.telesignal.data.login.dto.LoginDto
import com.example.telesignal.data.login.dto.RegisterDto
import com.example.telesignal.ui.chat.ChatActivity
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val cipherUtils = CipherUtils()

    private lateinit var loginService: LoginService

    private lateinit var emailEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button
    private lateinit var registerButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginService = LoginService(AuthTokenManager(requireActivity()))
        emailEditText = view.requireViewById(R.id.email)
        usernameEditText = view.requireViewById(R.id.username)
        passwordEditText = view.requireViewById(R.id.password)
        signInButton = view.requireViewById(R.id.login_button)
        registerButton = view.requireViewById(R.id.register_button)

        signInButton.setOnClickListener {
            val loginInfo =
                LoginDto(usernameEditText.text.toString(), passwordEditText.text.toString())
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    loginService.login(loginInfo)
                    Toast.makeText(context, "Successfully logged in.", Toast.LENGTH_SHORT).show()
                    redirectToChatActivity()
                } catch (ex: ClientRequestException) {
                    Log.w(
                            LOG_TAG,
                            "Invalid request response - status code: ${ex.response.status}, body: ${ex.response.bodyAsText()}"
                    )
                    Toast.makeText(context, "Failed to log in", Toast.LENGTH_SHORT).show()
                }
            }
        }

        registerButton.setOnClickListener {
            val encodedKey =
                Base64.encodeToString(cipherUtils.getRsaKeyPair().publicKey.encoded, Base64.DEFAULT)
            val registerInfo = RegisterDto(
                    emailEditText.text.toString(),
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString(),
                    passwordEditText.text.toString(),
                    encodedKey
            )
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    loginService.register(registerInfo)
                    Toast.makeText(context, "Successfully registered.", Toast.LENGTH_SHORT).show()
                    redirectToChatActivity()
                } catch (ex: ClientRequestException) {
                    Log.w(
                            LOG_TAG,
                            "Invalid request response - status code: ${ex.response.status}, body: ${ex.response.bodyAsText()}"
                    )
                    Toast.makeText(context, "Failed to register", Toast.LENGTH_SHORT).show()
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