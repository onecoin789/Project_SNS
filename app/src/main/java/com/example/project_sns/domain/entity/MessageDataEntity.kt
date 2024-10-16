package com.example.project_sns.domain.entity

data class MessageDataEntity(
    val uid: String,
    val chatRoomId: String,
    val messageId: String,
    val message: String,
    val sendAt: String
)
