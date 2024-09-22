package com.example.project_sns.domain.model

data class CommentDataEntity(
    val uid: String,
    val postId: String,
    val commentId: String,
    val comment: String,
    val commentAt: String,
    val editedAt: String?,
    val reCommentSize: Int
)