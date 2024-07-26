package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.repository.AuthRepository
import com.example.project_sns.ui.view.signup.data.FirebaseUserData
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, data: FirebaseUserData) : Result<String> {
        return authRepository.signUp(email, password, data)
    }
}