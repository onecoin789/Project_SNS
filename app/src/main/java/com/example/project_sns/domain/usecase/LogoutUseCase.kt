package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke() {
        authRepository.logout()
    }
}