package com.example.project_sns.data.response

import com.example.project_sns.data.mapper.toEntity
import com.example.project_sns.domain.MessageViewType
import com.example.project_sns.domain.entity.MessageDataEntity

data class MessageDataResponse(
    val uid: String = "",
    val chatRoomId: String = "",
    val messageId: String = "",
    val message: String? = null,
    val imageList: List<ImageDataResponse>? = null,
    val sendAt: String = "",
    val read: List<Map<String, Boolean>> = emptyList(),
    val type: MessageViewType = MessageViewType.TEXT_MESSAGE
)

fun MessageDataResponse.toEntity() = MessageDataEntity(
    uid = uid,
    chatRoomId = chatRoomId,
    messageId = messageId,
    message = message,
    imageList = imageList?.map { it.toEntity() },
    sendAt = sendAt,
    read = read,
    type = type
)

fun List<MessageDataResponse>.toMessageListEntity(): List<MessageDataEntity> {
    return this.map { it.toEntity() }
}