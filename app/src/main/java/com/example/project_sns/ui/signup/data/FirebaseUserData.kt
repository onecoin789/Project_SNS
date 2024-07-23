package com.example.project_sns.ui.signup.data

import com.google.firebase.Timestamp


data class FirebaseUserData(
    val uid : String,
    val name : String,
    val email : String,
    val image : String? = "",
    val createdAt : Timestamp
)
