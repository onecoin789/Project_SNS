package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckMessageDataUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(chatRoomId: String): Flow<Boolean> {
        return dataRepository.checkMessageData(chatRoomId)
    }
}