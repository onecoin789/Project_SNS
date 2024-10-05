package com.example.project_sns.ui.view.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PostDataModel(
    val uid: String,
    val postId: String,
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
    var placeUrl: String?,
    var addressName: String?,
    var lat: Double?,
    var lng: Double?
): Parcelable
