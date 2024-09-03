package com.example.project_sns.ui.view.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PostDataModel(
    val uid: String,
    val postId: String,
    val profileImage: String?,
    val name: String,
    val email: String,
    val imageList: List<ImageDataModel>?,
    val postText: String?,
    val createdAt: String,
    val editedAt: String?,
    val mapData: MapDataModel?
) : Parcelable

@Parcelize
data class ImageDataModel(
    val downloadUrl: String,
    val imageUri: String,
    val imageType: String
): Parcelable

@Parcelize
data class MapDataModel(
    var placeName: String?,
    var addressName: String?,
    var lat: Double?,
    var lng: Double?
): Parcelable

@Parcelize
data class CommentDataModel(
    val commentId: String,
    val comment: String,
    val commentAt: String,
    val uid: String,
    val name: String,
    val email: String,
    val profileImage: String?,
): Parcelable

@Parcelize
data class ReCommentDataModel(
    val commentId: String,
    val comment: String,
    val commentAt: String,
    val uid: String,
    val name: String,
    val email: String,
    val profileImage: String?
): Parcelable