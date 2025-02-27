package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.entity.RequestEntity
import com.example.project_sns.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRequestDataUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(lastVisibleItem: Flow<Int>): Flow<List<RequestEntity>> {
        return authRepository.getRequestList(lastVisibleItem)
    }
}