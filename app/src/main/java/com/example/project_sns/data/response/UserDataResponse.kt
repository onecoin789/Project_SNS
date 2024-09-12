package com.example.project_sns.data.response

data class UserDataResponse(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val profileImage: String? = null,
    val createdAt : String = "",
    val intro: String? = ""
)