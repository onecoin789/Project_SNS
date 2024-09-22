package com.example.project_sns.data.response

data class PostResponse(
    val userData: UserDataResponse,
    val postData: PostDataResponse
)

data class CommentResponse(
    val userData: UserDataResponse,
    val commentData: CommentDataResponse
)

data class ReCommentResponse(
    val userData: UserDataResponse,
    val reCommentData: ReCommentDataResponse
)