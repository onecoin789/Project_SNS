package com.example.project_sns.domain.usecase

import android.net.Uri
import com.example.project_sns.domain.repository.AuthRepository
import com.example.project_sns.ui.view.signup.model.FirebaseUserData
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, password: String, imageUri: String) : Result<String> {
        return authRepository.signUp(name, email, password, imageUri)
    }
}