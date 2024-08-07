package com.example.project_sns.ui.view.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostDataModel(
    val postId: String,
    val profileImage: String?,
    val name: String,
    val email: String,
    val image: String,
    val postText: String,
    val lat: Double,
    val lng: Double,
    val createdAt: String
) : Parcelable