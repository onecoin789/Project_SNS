package com.example.project_sns.ui.model

import android.net.Uri
import android.os.Parcelable
import com.example.project_sns.domain.MessageViewType
import com.example.project_sns.domain.entity.MessageDataEntity
import com.example.project_sns.domain.entity.UploadMessageDataEntity
import com.example.project_sns.ui.mapper.toEntity
import com.example.project_sns.ui.mapper.toModel
import com.example.project_sns.ui.util.chatTimeFormat
import com.example.project_sns.ui.util.stringToLocalTime
import kotlinx.parcelize.Parcelize


@Parcelize
data class MessageDataModel(
    val uid: String,
    val chatRoomId: String,
    val messageId: String,
    val message: String?,
    val imageList: List<ImageDataModel>?,
    val sendAt: String,
    val read: List<Map<String, Boolean>>,
    val type: MessageViewType
) : Parcelable

@Parcelize
data class UploadMessageDataModel(
    val uid: String,
    val chatRoomId: String,
    val messageId: String,
    val message: String?,
    val imageList: List<Uri>?,
    val sendAt: String,
    val read: List<Map<String, Boolean>>,
    val type: MessageViewType
) : Parcelable

fun MessageDataEntity.toModel() = MessageDataModel(
    uid = uid,
    chatRoomId = chatRoomId,
    messageId = messageId,
    message = message,
    imageList = imageList?.map { it.toModel() },
    sendAt = chatTimeFormat(stringToLocalTime(sendAt)),
    read = read,
    type = type
)

fun MessageDataModel.toEntity() = MessageDataEntity(
    uid = uid,
    chatRoomId = chatRoomId,
    messageId = messageId,
    message = message,
    imageList = imageList?.map { it.toEntity() },
    sendAt = sendAt,
    read = read,
    type = type
)

fun List<MessageDataEntity>.toMessageListModel(): List<MessageDataModel> {
    return this.map { it.toModel() }
}

fun UploadMessageDataModel.toEntity() = UploadMessageDataEntity(
    uid, chatRoomId, messageId, message, imageList, sendAt, read, type
)

