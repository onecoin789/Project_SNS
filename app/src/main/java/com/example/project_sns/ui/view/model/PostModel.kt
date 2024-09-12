package com.example.project_sns.ui.view.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostModel(
    val userData: UserDataModel,
    val postData: PostDataModel
) : Parcelable