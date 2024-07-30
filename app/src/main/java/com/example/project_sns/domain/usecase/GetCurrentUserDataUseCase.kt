package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.model.CurrentUserEntity
import com.example.project_sns.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCurrentUserDataUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<CurrentUserEntity?> {
        return authRepository.getCurrentUserData()
    }
}