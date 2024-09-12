package com.example.project_sns.ui.view.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserDataModel(
    val uid: String,
    val name: String,
    val email: String,
    val profileImage: String?,
    val createdAt : String,
    val intro: String?
): Parcelable
