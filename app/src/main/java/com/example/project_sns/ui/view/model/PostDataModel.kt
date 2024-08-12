package com.example.project_sns.ui.view.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostDataModel(
    val postId: String,
    val profileImage: String?,
    val name: String,
    val email: String,
    val image: List<Uri>?,
    val postText: String,
    val lat: Double?,
    val lng: Double?,
    val placeName: String?,
    val createdAt: String,
    val commentData: CommentDataModel?
) : Parcelable

@Parcelize
data class CommentDataModel(
    val commenterProfile: String?,
    val commenterEmail: String,
    val commenterName: String,
    val comment: String
): Parcelable