package com.example.telesignal.ui.chat.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.telesignal.R
import com.example.telesignal.data.chat.ChatClient
import com.example.telesignal.data.chat.dto.MessageDto
import com.example.telesignal.data.login.AuthTokenManager
import com.example.telesignal.ui.chat.utils.ChatAdapter

class ChatFragment : Fragment(R.layout.fragment_chat) {

    private lateinit var tokenManager: AuthTokenManager
    private val chatClient = ChatClient(BACKEND_HOST + CHAT_PATH)
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tokenManager = AuthTokenManager(requireActivity())
        addRecyclerView(view)
        addMessageSending(view)
        addMessageReceiving()
    }

    private fun addRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.chat_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ChatAdapter(ArrayList())
        recyclerView.adapter = adapter
    }

    private fun addMessageSending(view: View) {
        val button: Button = view.findViewById(R.id.chat_send_button)
        val messageTextView: EditText = view.findViewById(R.id.chat_edit_text_view)
        button.setOnClickListener {
            chatClient.sendMessage(
                    tokenManager.getUsername().orEmpty(),
                    messageTextView.text.toString()
            )
            messageTextView.text.clear()
        }
    }

    private fun addMessageReceiving() {
        chatClient.setOnMessageHandler { user: String, message: String ->
            requireActivity().runOnUiThread { adapter.addMessage(MessageDto(user, message)) }
        }
    }


    companion object {

        private const val BACKEND_HOST = "http://10.0.2.2:5022"
        private const val CHAT_PATH = "/chatHub"
    }

}