package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.entity.UserDataEntity
import com.example.project_sns.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFriendListDataUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(uid: String): Flow<List<UserDataEntity>> {
        return authRepository.getFriendList(uid)
    }
}