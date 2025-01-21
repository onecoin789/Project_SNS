package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LogInUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) : Flow<String> {
        return authRepository.logIn(email, password)
    }
}