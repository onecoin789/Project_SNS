package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CancelFriendRequestUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(fromUid: String, toUid: String): Flow<Boolean> {
        return authRepository.cancelFriendRequest(fromUid, toUid)
    }
}