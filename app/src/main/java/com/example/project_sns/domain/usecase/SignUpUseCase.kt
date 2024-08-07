package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, password: String, imageUri: String?) : Result<String> {
        return authRepository.signUp(name, email, password, imageUri)
    }
}