package com.example.project_sns.domain.entity

data class UserDataEntity(
    val uid: String,
    val name: String,
    val email: String,
    val profileImage: String?,
    val createdAt: String,
    val intro: String?
)