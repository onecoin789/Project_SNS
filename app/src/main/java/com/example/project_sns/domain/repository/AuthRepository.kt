package com.example.project_sns.domain.repository

import com.example.project_sns.domain.model.CurrentUserEntity
import com.example.project_sns.ui.view.signup.model.FirebaseUserData
import kotlinx.coroutines.flow.Flow


interface AuthRepository {

    suspend fun signUp(email: String, password: String, data: FirebaseUserData) : Result<String>

    suspend fun logIn(email: String, password: String) : Result<String>

    suspend fun logout(): Result<String>

    suspend fun getCurrentUserData() : Flow<CurrentUserEntity?>
}