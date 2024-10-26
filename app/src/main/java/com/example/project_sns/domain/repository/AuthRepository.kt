package com.example.project_sns.domain.repository

import com.example.project_sns.domain.entity.RequestEntity
import com.example.project_sns.domain.entity.UserDataEntity
import kotlinx.coroutines.flow.Flow


interface AuthRepository {

    suspend fun signUp(name: String, email: String, password: String, profileImage: String?, createdAt: String) : Result<String>

    suspend fun logIn(email: String, password: String) : Result<String>

    suspend fun logout(): Result<String>

    suspend fun getCurrentUserData() : Flow<UserDataEntity?>

    suspend fun getUserByUid(uid: String): Flow<UserDataEntity?>

    suspend fun editProfile(uid: String, name: String, email: String, newProfile: String?, beforeProfile: String?, intro: String?, createdAt: String): Result<String>

    suspend fun kakaoLogin(accessToken: String): Result<String>

    suspend fun checkRequestList(): Flow<Boolean>

    suspend fun requestFriend(requestId: String, fromUid: String, toUid: String): Flow<Boolean>

    suspend fun getRequestList(lastVisibleItem: Flow<Int>): Flow<List<RequestEntity>>

    suspend fun acceptFriendRequest(requestId: String, fromUid: String, toUid: String): Flow<Boolean>

    suspend fun rejectFriendRequest(requestId: String): Flow<Boolean>

    suspend fun checkFriendRequest(toUid: String): Flow<Boolean>

    suspend fun cancelFriendRequest(fromUid: String, toUid: String): Flow<Boolean>

    suspend fun getFriendList(uid: String): Flow<List<UserDataEntity>>

    suspend fun deleteFriend(fromUid: String, toUid: String): Flow<Boolean>

}