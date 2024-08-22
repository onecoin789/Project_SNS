package com.example.project_sns.data.response

import android.net.Uri

data class PostDataResponse(
    val uid: String = "",
    val postId: String = "",
    val profileImage: String? = null,
    val name: String = "",
    val email: String = "",
    val image: List<String>? = emptyList(),
    val postText: String? = null,
    val createdAt: String = "",
    val mapData: MapDataResponse? = null,
    val commentData: CommentDataResponse? = null
)

data class MapDataResponse(
    var placeName: String? = null,
    var addressName: String? = null,
    var lat: Double? = null,
    var lng: Double? = null
)

data class CommentDataResponse(
    val commenterProfile: String?,
    val commenterEmail: String,
    val commenterName: String,
    val comment: String
)
