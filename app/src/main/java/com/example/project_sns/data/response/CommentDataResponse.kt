package com.example.project_sns.data.response

data class CommentDataResponse(
    val uid: String = "",
    val postId: String = "",
    val commentId: String = "",
    val comment: String = "",
    val commentAt: String = "",
    val editedAt: String? = null,
    val reCommentSize: Int = 0
)