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
    val image: List<String>?,
    val postText: String?,
    val createdAt: String,
    val mapData: MapDataModel?,
    val commentData: CommentDataModel?
) : Parcelable

@Parcelize
data class MapDataModel(
    var placeName: String?,
    var addressName: String?,
    var lat: Double?,
    var lng: Double?
): Parcelable

@Parcelize
data class CommentDataModel(
    val commenterProfile: String?,
    val commenterEmail: String,
    val commenterName: String,
    val comment: String
): Parcelable