package com.example.project_sns.ui.model

import android.os.Parcelable
import com.example.project_sns.domain.entity.ChatRoomDataEntity
import com.example.project_sns.ui.util.chatListDateFormat
import com.example.project_sns.ui.util.stringToLocalTime
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatRoomDataModel(
    val chatRoomId: String,
    val lastMessage: String,
    val lastSendAt: String,
    val participant: List<String>
): Parcelable

fun ChatRoomDataEntity.toModel() = ChatRoomDataModel(
    chatRoomId = chatRoomId, lastMessage = lastMessage, lastSendAt = chatListDateFormat(stringToLocalTime(lastSendAt)), participant = participant
)

fun List<ChatRoomDataEntity>.toChatRoomListModel(): List<ChatRoomDataModel> {
    return this.map { it.toModel() }
}