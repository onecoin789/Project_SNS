package com.example.project_sns.ui

data class CurrentUserModel(
    val uid: String,
    val name: String,
    val email: String,
    val profileImage: String?,
    val createdAt : String
)
