package com.example.project_sns.domain.entity

data class ChatRoomDataEntity(
    val chatRoomId: String,
    val lastMessage: String,
    val lastSendAt: String,
    val participant: List<String>
)
