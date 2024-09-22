package com.example.project_sns.data.response

data class PostDataResponse(
    val uid: String = "",
    val postId: String = "",
    val imageList: List<ImageDataResponse>? = emptyList(),
    val postText: String? = null,
    val createdAt: String = "",
    val editedAt: String? = null,
    val mapData: MapDataResponse? = null
)

data class ImageDataResponse(
    val downloadUrl: String = "",
    val imageUri: String = "",
    val imageType: String = ""
)

data class MapDataResponse(
    var placeName: String? = null,
    var addressName: String? = null,
    var lat: Double? = null,
    var lng: Double? = null
)

