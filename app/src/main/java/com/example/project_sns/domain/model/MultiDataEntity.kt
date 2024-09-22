package com.example.project_sns.domain.model

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