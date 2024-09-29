package com.example.project_sns.ui.view.model

import android.os.Parcelable
import com.example.project_sns.data.response.ReCommentDataResponse
import com.example.project_sns.data.response.UserDataResponse
import com.example.project_sns.domain.model.CommentDataEntity
import com.example.project_sns.domain.model.UserDataEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostModel(
    val userData: UserDataModel,
    val postData: PostDataModel
) : Parcelable

@Parcelize
data class CommentModel (
    val userData: UserDataModel,
    val commentData: CommentDataModel
): Parcelable

@Parcelize
data class ReCommentModel(
    val userData: UserDataModel,
    val reCommentData: ReCommentDataModel
): Parcelable

@Parcelize
data class RequestModel(
    val fromUid: UserDataModel,
    val toUid: String
): Parcelable