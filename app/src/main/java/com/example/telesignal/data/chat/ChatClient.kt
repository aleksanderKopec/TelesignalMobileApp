package com.example.telesignal.data.chat

import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder

class ChatClient(url: String) {

    private val hubConnection: HubConnection

    init {
        hubConnection = HubConnectionBuilder
                .create(url)
                .build()
        hubConnection.start().blockingAwait()
    }

    fun sendMessage(username: String, message: String) {
        hubConnection.send(SEND_METHOD, username, message)
    }


    fun setOnMessageHandler(onMessageHandler: (username: String, message: String) -> Unit) {
        hubConnection.on(
                RECEIVE_METHOD,
                onMessageHandler,
                String::class.java,
                String::class.java
        )
    }

    companion object {

        private const val SEND_METHOD = "SendMessage"
        private const val RECEIVE_METHOD = "ReceiveMessage"
    }
}