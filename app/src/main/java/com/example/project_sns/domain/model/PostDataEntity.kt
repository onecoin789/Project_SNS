package com.example.project_sns.domain.model

data class PostDataEntity(
    val uid: String,
    val postId: String,
    val profileImage: String?,
    val name: String,
    val email: String,
    val imageList: List<ImageDataEntity>?,
    val postText: String?,
    val createdAt: String,
    val mapData: MapDataEntity?,
    val commentData: CommentDataEntity?,
)

data class ImageDataEntity(
    val imageUri: String,
    val imageType: String
)

data class MapDataEntity(
    var placeName: String?,
    var addressName: String?,
    var lat: Double?,
    var lng: Double?
)

data class CommentDataEntity(
    val commenterProfile: String?,
    val commenterEmail: String,
    val commenterName: String,
    val comment: String
)