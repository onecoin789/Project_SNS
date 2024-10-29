package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.entity.MessageEntity
import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatMessageDataUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(chatRoomId: String): Flow<List<MessageEntity>> {
        return dataRepository.getChatMessageData(chatRoomId)
    }
}