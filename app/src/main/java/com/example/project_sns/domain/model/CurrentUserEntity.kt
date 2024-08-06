package com.example.project_sns.domain.model

import com.google.firebase.Timestamp

data class CurrentUserEntity(
    val uid: String,
    val name: String,
    val email: String,
    val profileImage: String? = "",
    val createdAt: String
)