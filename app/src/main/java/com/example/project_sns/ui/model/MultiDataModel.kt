package com.example.project_sns.ui.model

import android.os.Parcelable
import androidx.room.Entity
import com.example.project_sns.domain.entity.MessageDataEntity
import com.example.project_sns.domain.entity.UserDataEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostModel(
    val userData: UserDataModel,
    val postData: PostDataModel
) : Parcelable

@Parcelize
data class CommentModel (
    val userData: UserDataModel,
    val commentData: CommentDataModel
): Parcelable

@Parcelize
data class ReCommentModel(
    val userData: UserDataModel,
    val reCommentData: ReCommentDataModel
): Parcelable

@Parcelize
data class RequestModel(
    val requestId: String,
    val fromUid: UserDataModel,
    val toUid: String
): Parcelable

@Parcelize
data class ChatRoomModel(
    val userData: UserDataModel,
    val chatRoomData: ChatRoomDataModel
): Parcelable

@Parcelize
data class MessageModel(
    val userData: UserDataModel,
    val messageData: MessageDataModel
): Parcelable