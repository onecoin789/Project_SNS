package com.example.project_sns.domain.repository

import com.example.project_sns.domain.model.CurrentUserEntity
import kotlinx.coroutines.flow.Flow


interface AuthRepository {

    suspend fun signUp(name: String, email: String, password: String, profileImage: String?, createdAt: String) : Result<String>

    suspend fun logIn(email: String, password: String) : Result<String>

    suspend fun logout(): Result<String>

    suspend fun getCurrentUserData() : Flow<CurrentUserEntity?>

    suspend fun editProfile(uid: String, name: String, email: String, newProfile: String?, beforeProfile: String?, intro: String?, createdAt: String): Result<String>
}