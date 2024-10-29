package com.example.project_sns.domain.entity

import com.example.project_sns.data.response.ChatRoomDataResponse
import com.example.project_sns.data.response.UserDataResponse

data class PostEntity (
    val userData: UserDataEntity,
    val postData: PostDataEntity
)

data class CommentEntity (
    val userData: UserDataEntity,
    val commentData: CommentDataEntity
)

data class ReCommentEntity(
    val userData: UserDataEntity,
    val reCommentData: ReCommentDataEntity
)

data class RequestEntity(
    val requestId: String,
    val fromUid: UserDataEntity,
    val toUid: String
)

data class ChatRoomEntity(
    val userData: UserDataEntity,
    val chatRoomData: ChatRoomDataEntity
)

data class MessageEntity(
    val userData: UserDataEntity,
    val messageData: MessageDataEntity
)