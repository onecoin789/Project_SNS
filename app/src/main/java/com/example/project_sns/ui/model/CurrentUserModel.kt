package com.example.project_sns.ui.model

import com.google.firebase.Timestamp

data class CurrentUserModel(
    val name: String,
    val email: String,
    val profileImage: String? = "",
    val createdAt : Timestamp
)
