package com.example.project_sns.domain.entity

import android.net.Uri
import com.example.project_sns.domain.MessageViewType

data class MessageDataEntity(
    val uid: String,
    val chatRoomId: String,
    val messageId: String,
    val message: String?,
    val imageList: List<ImageDataEntity>?,
    val sendAt: String,
    val read: List<Map<String, Boolean>>,
    val type: MessageViewType
)



//업로드 용 entity
data class UploadMessageDataEntity(
    val uid: String,
    val chatRoomId: String,
    val messageId: String,
    val message: String?,
    val imageList: List<Uri>?,
    val sendAt: String,
    val read: List<Map<String, Boolean>>,
    val type: MessageViewType
)
