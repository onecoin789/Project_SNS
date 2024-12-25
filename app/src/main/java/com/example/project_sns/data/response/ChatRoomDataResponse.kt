package com.example.project_sns.data.response

import com.example.project_sns.domain.entity.ChatRoomDataEntity
import com.example.project_sns.domain.entity.LastMessageDataEntity

data class ChatRoomDataResponse(
    val chatRoomId: String = "",
    val lastMessageData: LastMessageDataResponse = LastMessageDataResponse(),
    val unReadMessage: Int = 0,
    val participant: List<String> = emptyList(), // 참여자
    val chatRoomSession: List<Map<String, Boolean>> = emptyList()
)

data class LastMessageDataResponse(
    val lastMessage: String = "",
    val lastSendAt: String = "",
    val lastMessageSender: String = ""
)

fun ChatRoomDataResponse.toEntity() = ChatRoomDataEntity(
    chatRoomId = chatRoomId,
    lastMessageData = lastMessageData.toEntity(),
    unReadMessage = unReadMessage,
    participant = participant,
    chatRoomSession = chatRoomSession
)

fun LastMessageDataResponse.toEntity() = LastMessageDataEntity(
    lastMessage = lastMessage,
    lastSendAt = lastSendAt,
    lastMessageSender = lastMessageSender
)

fun List<ChatRoomDataResponse>.toChatRoomListEntity(): List<ChatRoomDataEntity> {
    return this.map { it.toEntity() }
}

