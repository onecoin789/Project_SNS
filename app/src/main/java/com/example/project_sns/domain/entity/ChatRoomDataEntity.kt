package com.example.project_sns.domain.entity

data class ChatRoomDataEntity(
    val chatRoomId: String,
    val lastMessageData: LastMessageDataEntity,
    val unReadMessage: Int,
    val participant: List<String>,
    val chatRoomSession: List<Map<String, Boolean>>
)

data class LastMessageDataEntity(
    val lastMessage: String,
    val lastSendAt: String,
    val lastMessageSender: String
)
