package com.example.project_sns.ui.view.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommentDataModel(
    val uid: String,
    val postId: String,
    val commentId: String,
    val comment: String,
    val commentAt: String,
    val editedAt: String?,
    val reCommentSize: Int
): Parcelable
