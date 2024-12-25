package com.example.project_sns.ui.model

import android.os.Parcelable
import com.example.project_sns.domain.entity.ChatRoomDataEntity
import com.example.project_sns.domain.entity.LastMessageDataEntity
import com.example.project_sns.ui.util.chatListDateFormat
import com.example.project_sns.ui.util.stringToLocalTime
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatRoomDataModel(
    val chatRoomId: String,
    val lastMessageData: LastMessageDataModel,
    val unReadMessage: Int,
    val participant: List<String>,
    val chatRoomSession: List<Map<String, Boolean>>
) : Parcelable


@Parcelize
data class LastMessageDataModel(
    val lastMessage: String,
    val lastSendAt: String,
    val lastMessageSender: String
): Parcelable

fun ChatRoomDataEntity.toModel() = ChatRoomDataModel(
    chatRoomId = chatRoomId,
    lastMessageData = lastMessageData.toModel(),
    unReadMessage = unReadMessage,
    participant = participant,
    chatRoomSession = chatRoomSession
)

fun LastMessageDataEntity.toModel() = LastMessageDataModel(
    lastMessage = lastMessage,
    lastSendAt = chatListDateFormat(stringToLocalTime(lastSendAt)),
    lastMessageSender = lastMessageSender
)

fun List<ChatRoomDataEntity>.toChatRoomListModel(): List<ChatRoomDataModel> {
    return this.map { it.toModel() }
}