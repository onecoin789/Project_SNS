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

data class CommentDataEntity(
    val commentId: String,
    val comment: String,
    val commentAt: String,
    val uid: String,
    val name: String,
    val email: String,
    val profileImage: String?,
    val reCommentData: List<ReCommentDataEntity>?
)

data class ReCommentDataEntity(
    val commentId: String,
    val comment: String,
    val commentAt: String,
    val uid: String,
    val name: String,
    val email: String,
    val profileImage: String?
)