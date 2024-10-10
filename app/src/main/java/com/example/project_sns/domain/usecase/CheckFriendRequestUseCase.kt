package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckFriendRequestUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(toUid: String): Flow<Boolean> {
        return authRepository.checkFriendRequest(toUid)
    }
}