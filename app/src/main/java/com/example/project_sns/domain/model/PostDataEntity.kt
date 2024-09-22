package com.example.project_sns.domain.model

data class PostDataEntity(
    val uid: String,
    val postId: String,
    val imageList: List<ImageDataEntity>?,
    val postText: String?,
    val createdAt: String,
    val editedAt: String?,
    val mapData: MapDataEntity?
)

data class ImageDataEntity(
    val downloadUrl: String,
    val imageUri: String,
    val imageType: String
)

data class MapDataEntity(
    var placeName: String?,
    var addressName: String?,
    var lat: Double?,
    var lng: Double?
)
