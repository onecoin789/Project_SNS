package com.example.project_sns.data.response

import com.example.project_sns.domain.entity.ChatRoomDataEntity

data class ChatRoomDataResponse(
    val chatRoomId: String = "",
    val senderUid: String = "", // 보내는 사람
    val recipientUid: String = "" // 받는 사람
)

fun ChatRoomDataResponse.toEntity() = ChatRoomDataEntity(
    chatRoomId = chatRoomId, senderUid = senderUid, recipientUid = recipientUid
)

