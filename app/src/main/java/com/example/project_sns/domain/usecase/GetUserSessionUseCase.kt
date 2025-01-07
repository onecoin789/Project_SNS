package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserSessionUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(recipientUid: String, chatRoomId: String): Flow<Boolean> {
        return dataRepository.getUserSession(recipientUid, chatRoomId)
    }
}