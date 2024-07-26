package com.example.project_sns.ui.view.signup.data

import com.google.firebase.Timestamp


data class FirebaseUserData(
    val name : String,
    val email : String,
    val image : String? = "",
    val createdAt : Timestamp
)
