package com.example.project_sns.domain.entity

data class ChatRoomDataEntity(
    val chatRoomId: String,
    val senderUid: String, // 보내는 사람
    val recipientUid: String // 받는 사람
)
