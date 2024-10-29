package com.example.project_sns.ui.model

import android.os.Parcelable
import com.example.project_sns.domain.entity.MessageDataEntity
import com.example.project_sns.ui.util.chatTimeFormat
import com.example.project_sns.ui.util.stringToLocalTime
import kotlinx.parcelize.Parcelize


@Parcelize
data class MessageDataModel(
    val uid: String,
    val chatRoomId: String,
    val messageId: String,
    val message: String,
    val sendAt: String
): Parcelable

fun MessageDataEntity.toModel() = MessageDataModel(
    uid = uid, chatRoomId = chatRoomId, messageId = messageId, message = message, sendAt = chatTimeFormat(stringToLocalTime(sendAt))
)

fun MessageDataModel.toEntity() = MessageDataEntity(
    uid = uid, chatRoomId = chatRoomId, messageId = messageId, message = message, sendAt = sendAt
)

fun List<MessageDataEntity>.toMessageListModel(): List<MessageDataModel> {
    return this.map { it.toModel() }
}
