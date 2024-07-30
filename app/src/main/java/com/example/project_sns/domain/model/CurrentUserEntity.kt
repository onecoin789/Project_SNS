package com.example.project_sns.domain.model

import com.google.firebase.Timestamp

data class CurrentUserEntity(
    val name: String,
    val email: String,
    val profileImage: String? = "",
    val createdAt : Timestamp
)