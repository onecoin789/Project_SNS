package com.example.project_sns.data.response

data class PostDataResponse(
    val postId: String = "",
    val profileImage: String? = null,
    val name: String = "",
    val email: String = "",
    val image: String = "",
    val postText: String = "",
    val lat: Double? = null,
    val lng: Double? = null,
    val placeName: String? = null,
    val createdAt: String = "",
    val commentData: CommentDataResponse? = null
)

data class CommentDataResponse(
    val commenterProfile: String?,
    val commenterEmail: String,
    val commenterName: String,
    val comment: String
)
