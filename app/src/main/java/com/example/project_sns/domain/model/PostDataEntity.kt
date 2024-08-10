package com.example.project_sns.domain.model

data class PostDataEntity(
    val postId: String,
    val profileImage: String?,
    val name: String,
    val email: String,
    val image: String,
    val postText: String,
    val lat: Double?,
    val lng: Double?,
    val placeName: String?,
    val createdAt: String,
    val commentData: CommentDataEntity?
)

data class CommentDataEntity(
    val commenterProfile: String?,
    val commenterEmail: String,
    val commenterName: String,
    val comment: String
)