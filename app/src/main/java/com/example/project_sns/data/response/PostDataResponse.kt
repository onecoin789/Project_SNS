package com.example.project_sns.data.response

data class PostDataResponse(
    val postId: String = "",
    val profileImage: String = "",
    val name: String = "",
    val email: String = "",
    val image: String = "",
    val postText: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val createdAt: String = ""
)
