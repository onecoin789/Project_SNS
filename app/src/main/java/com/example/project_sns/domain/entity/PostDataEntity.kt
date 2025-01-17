package com.example.project_sns.domain.entity

data class PostDataEntity(
    val uid: String,
    val postId: String,
    val imageList: List<ImageDataEntity>?,
    val postText: String?,
    val createdAt: String,
    val editedAt: String?,
    val mapData: MapDataEntity?,
    val likePost: List<String>
)

data class ImageDataEntity(
    val downloadUrl: String,
    val imageUri: String,
    val imageType: String
)

data class MapDataEntity(
    var placeName: String?,
    var placeUrl: String?,
    var addressName: String?,
    var lat: Double?,
    var lng: Double?
)
