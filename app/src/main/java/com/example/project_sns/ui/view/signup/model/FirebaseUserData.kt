package com.example.project_sns.ui.view.signup.model

import com.google.firebase.Timestamp


data class FirebaseUserData(
    val name : String,
    val email : String,
    val profileImage : String? = "",
    val createdAt : Timestamp
)
