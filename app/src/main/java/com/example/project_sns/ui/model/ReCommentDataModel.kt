package com.example.project_sns.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ReCommentDataModel(
    val uid: String,
    val commentId: String,
    val reCommentId: String,
    val comment: String,
    val commentAt: String,
    val editedAt: String?
): Parcelable
