package com.example.project_sns.domain.entity

data class ReCommentDataEntity(
    val uid: String,
    val commentId: String,
    val reCommentId: String,
    val comment: String,
    val commentAt: String,
    val editedAt: String?
)