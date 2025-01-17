package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CancelAccountUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(uid: String, confirmEmail: String): Flow<Boolean> {
        return authRepository.cancelAccount(uid = uid, confirmEmail = confirmEmail)
    }
}