package com.example.project_sns.domain.model

data class PostDataEntity(
    val postId: String,
    val profileImage: String?,
    val name: String,
    val email: String,
    val image: String,
    val postText: String,
    val lat: Double,
    val lng: Double,
    val createdAt: String
)