package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.entity.MessageDataEntity
import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(chatRoomId: String, messageData: MessageDataEntity): Flow<Boolean> {
        return dataRepository.sendMessage(chatRoomId, messageData)
    }
}