package com.example.telesignal.ui.chat.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.telesignal.R
import com.example.telesignal.data.chat.ChatClient
import com.example.telesignal.data.login.AuthTokenManager

class JoinChatFragment : Fragment(R.layout.fragment_join_chat) {

    private lateinit var tokenManager: AuthTokenManager
    private lateinit var chatClient: ChatClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tokenManager = AuthTokenManager(requireActivity())
        chatClient = ChatClient.getInstance(tokenManager.getToken()?.token)
        allowJoiningRooms()
    }

    private fun allowJoiningRooms() {
        chatClient.addOnConnectHandler {
            tokenManager.setRoomId(it);
            startChatFragment()
        }
        chatClient.addOnGetRoomKeyMapHandler { _, keyMap ->
            chatClient.keyMap = keyMap as Map<String, String>
        }
        requireActivity().findViewById<Button>(R.id.chat_join_button).setOnClickListener {
            val roomName =
                requireActivity().findViewById<EditText>(R.id.chat_join_edit_text).text.toString()
            chatClient.joinRoom(roomName, tokenManager.getUserId())
        }

    }

    private fun startChatFragment() {
        val chatFragment = ChatFragment()
        val transaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.chat_fragment_container_view, chatFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}