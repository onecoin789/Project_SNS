package com.example.project_sns.ui.model

import com.example.project_sns.domain.entity.MessageDataEntity

data class MessageDataModel(
    val uid: String,
    val chatRoomId: String,
    val messageId: String,
    val message: String,
    val sendAt: String
)

fun MessageDataModel.toEntity() = MessageDataEntity(
    uid = uid, chatRoomId = chatRoomId, messageId = messageId, message = message, sendAt = sendAt
)
