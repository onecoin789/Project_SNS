package com.example.project_sns.data.response

import com.example.project_sns.domain.entity.MessageDataEntity

data class MessageDataResponse(
    val uid: String = "",
    val chatRoomId: String = "",
    val messageId: String = "",
    val message: String = "",
    val sendAt: String = ""
)

fun MessageDataResponse.toEntity() = MessageDataEntity(
    uid = uid, chatRoomId = chatRoomId, messageId = messageId, message = message, sendAt = sendAt
)

fun List<MessageDataResponse>.toMessageListEntity(): List<MessageDataEntity> {
    return this.map { it.toEntity() }
}