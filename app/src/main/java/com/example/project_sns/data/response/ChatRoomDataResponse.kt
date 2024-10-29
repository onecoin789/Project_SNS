package com.example.project_sns.data.response

import com.example.project_sns.domain.entity.ChatRoomDataEntity

data class ChatRoomDataResponse(
    val chatRoomId: String = "",
    val lastMessage: String = "",
    val lastSendAt: String = "",
    val participant: List<String> = emptyList() // 참여자
)

fun ChatRoomDataResponse.toEntity() = ChatRoomDataEntity(
    chatRoomId = chatRoomId, lastMessage = lastMessage, lastSendAt = lastSendAt, participant = participant
)

fun List<ChatRoomDataResponse>.toChatRoomListEntity(): List<ChatRoomDataEntity> {
    return this.map { it.toEntity() }
}

