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
import com.example.telesignal.data.cipher.CipherUtils
import com.example.telesignal.data.login.AuthTokenManager
import com.example.telesignal.ui.chat.utils.ChatAdapter
import com.example.telesignal.ui.chat.utils.Message

class ChatFragment : Fragment(R.layout.fragment_chat) {

    private val cipherUtils = CipherUtils()

    private lateinit var tokenManager: AuthTokenManager
    private lateinit var chatClient: ChatClient;
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tokenManager = AuthTokenManager(requireActivity())
        chatClient = ChatClient.getInstance(tokenManager.getToken()?.token)
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
                    tokenManager.getUsername(),
                    tokenManager.getUserId(),
                    tokenManager.getRoomId(),
                    messageTextView.text.toString()
            )
            messageTextView.text.clear()
        }
    }

    private fun addMessageReceiving() {
        chatClient.setOnMessageHandler {
            val base64Key = it.keyMap[tokenManager.getUsername()]!!
            val decryptedKey =
                cipherUtils.decryptFromBase64(
                        base64Key,
                        cipherUtils.getRsaKeyPair().privateKey,
                        CipherUtils.RSA_CIPHER_ALGORITHM
                )
            val decryptionKey = cipherUtils.parseAesKey(decryptedKey)
            val decryptedMessage = cipherUtils.decryptFromBase64(
                    it.encryptedMessage,
                    decryptionKey,
                    CipherUtils.AES_CIPHER_ALGORITHM
            )
            requireActivity().runOnUiThread {
                adapter.addMessage(
                        Message(
                                decryptedMessage,
                                it.authorName
                        )
                )
            }
            requireActivity().runOnUiThread {
                recyclerView.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }


    companion object {

    }

}