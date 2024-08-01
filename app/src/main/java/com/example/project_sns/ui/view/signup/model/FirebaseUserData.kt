package com.example.project_sns.ui.view.signup.model

import android.net.Uri
import com.google.firebase.Timestamp


data class FirebaseUserData(
    val uid: String,
    val name : String,
    val email : String,
    val profileImage : String?,
    val createdAt : Timestamp
)
