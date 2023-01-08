package com.example.telesignal.data.chat

import android.util.Base64
import android.util.Log
import com.example.telesignal.data.chat.dto.MessageDto
import com.example.telesignal.data.cipher.CipherUtils
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.Single

class ChatClient private constructor(token: String) {

    val hubConnection: HubConnection
    val cipherUtils = CipherUtils()
    var keyMap: Map<String, String> = HashMap()

    init {
        hubConnection = HubConnectionBuilder
                .create(BACKEND_HOST + CHAT_PATH)
                .withAccessTokenProvider(Single.just(token))
                .build()
        hubConnection.start().blockingAwait()
        hubConnection.onClosed {
            Log.i("ChatClient", "Hub connection closed")
        }
        hubConnection.send("Test", "This is a test message")
    }


    fun sendMessage(userId: String, roomId: String, message: String) {
        val aesKey = cipherUtils.getAesKey()
        val encryptedMessage = cipherUtils.encryptToBase64(
                message,
                aesKey,
                CipherUtils.AES_CIPHER_ALGORITHM
        )
        val encryptionKeysMap = HashMap<String, String>()
        keyMap.forEach { (username, publicKey) ->
            val rsaPublicKey = cipherUtils.parsePublicKey(publicKey)
            val encryptedAesKey = cipherUtils.encryptToBase64(
                    Base64.encodeToString(aesKey.encoded, Base64.DEFAULT),
                    rsaPublicKey,
                    CipherUtils.RSA_CIPHER_ALGORITHM
            )
            encryptionKeysMap[username] = encryptedAesKey
        }
        val messageDto = MessageDto(
                userId,
                roomId,
                encryptedMessage,
                encryptionKeysMap
        )
        Log.i("ChatClient", "Sending message: $messageDto")
        hubConnection.send(SEND_METHOD, messageDto)
    }


    fun setOnMessageHandler(onMessageHandler: (message: MessageDto) -> Unit) {
        hubConnection.on(
                RECEIVE_METHOD,
                onMessageHandler,
                MessageDto::class.java
        )
    }

    fun joinRoom(roomName: String, userId: String) {
        hubConnection.send(JOIN_ROOM, roomName, userId)
    }

    fun addOnConnectHandler(onConnectHandler: (roomId: String) -> Unit) {
        hubConnection.on(
                JOIN_ROOM,
                onConnectHandler,
                String::class.java,
        )
    }

    fun addOnGetRoomKeyMapHandler(onAllClientsHandler: (roomId: String, keyMap: Map<*, *>) -> Unit) {
        hubConnection.on(
                GET_ROOM_KEY_MAP,
                onAllClientsHandler,
                String::class.java,
                Map::class.java
        )
    }

    fun addOnNewUserInRoomHandler(onNewUserHandler: (username: String, publicKey: String) -> Unit) {
        hubConnection.on(
                NEW_USER_IN_ROOM,
                onNewUserHandler,
                String::class.java,
                String::class.java
        )
    }

    companion object {

        @JvmStatic
        private var instance: ChatClient? = null

        @JvmStatic
        fun getInstance(token: String?): ChatClient {
            if (instance == null) {
                instance = ChatClient(token.orEmpty())
            }
            return instance as ChatClient
        }

        private const val SEND_METHOD = "SendMessage"
        private const val RECEIVE_METHOD = "ReceiveMessage"
        private const val JOIN_ROOM = "ConnectToRoom"
        private const val GET_ROOM_KEY_MAP = "GetRoomKeyMap"
        private const val NEW_USER_IN_ROOM = "AddUserToRoom"
        private const val BACKEND_HOST = "http://10.0.2.2:5022"
        private const val CHAT_PATH = "/chatHub"
    }
}