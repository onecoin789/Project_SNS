package com.example.project_sns.data.response

import com.google.firebase.Timestamp

data class CurrentUserResponse(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val profileImage: String? = null,
    val createdAt : String = "",
    val intro: String? = ""
)