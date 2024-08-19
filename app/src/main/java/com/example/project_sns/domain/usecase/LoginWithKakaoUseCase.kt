package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithKakaoUseCase @Inject constructor(private val authRepository: AuthRepository){
    suspend operator fun invoke(accessToken: String): Result<String> {
        return authRepository.kakaoLogin(accessToken)
    }
}