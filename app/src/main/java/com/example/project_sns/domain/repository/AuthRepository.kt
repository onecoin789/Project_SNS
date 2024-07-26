package com.example.project_sns.domain.repository

import com.example.project_sns.ui.view.signup.data.FirebaseUserData


interface AuthRepository {

    suspend fun signUp(email: String, password: String, data: FirebaseUserData) : Result<String>

    suspend fun logIn(email: String, password: String)
}