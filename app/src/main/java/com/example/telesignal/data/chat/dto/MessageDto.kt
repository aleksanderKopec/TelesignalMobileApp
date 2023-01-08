package com.example.telesignal.data.chat.dto

data class MessageDto(
        val authorId: String,
        val roomId: String,
        val encryptedMessage: String,
        val keyMap: Map<String, String>
)
