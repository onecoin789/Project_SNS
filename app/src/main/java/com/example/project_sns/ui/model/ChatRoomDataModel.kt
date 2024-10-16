package com.example.project_sns.ui.model

import com.example.project_sns.domain.entity.ChatRoomDataEntity

data class ChatRoomDataModel(
    val chatRoomId: String,
    val senderUid: String, // 보내는 사람
    val recipientUid: String // 받는 사람
)

fun ChatRoomDataEntity.toModel() = ChatRoomDataModel(
    chatRoomId = chatRoomId, senderUid = senderUid, recipientUid = recipientUid
)