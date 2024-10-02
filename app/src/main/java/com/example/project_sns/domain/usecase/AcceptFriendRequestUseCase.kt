package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AcceptFriendRequestUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(requestId: String, from: String, toUid: String): Flow<Boolean> {
        return authRepository.acceptFriendRequest(requestId, from, toUid)
    }
}