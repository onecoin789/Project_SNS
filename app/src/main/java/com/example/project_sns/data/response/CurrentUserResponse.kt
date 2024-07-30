package com.example.project_sns.data.response

import com.google.firebase.Timestamp

data class CurrentUserResponse(
    val name: String = "",
    val email: String = "",
    val profileImage: String? = "",
    val createdAt : Timestamp = Timestamp.now()
)