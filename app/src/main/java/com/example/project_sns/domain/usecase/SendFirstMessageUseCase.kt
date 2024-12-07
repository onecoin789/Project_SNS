package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.entity.UploadMessageDataEntity
import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendFirstMessageUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(chatRoomId: String, senderUid: String, recipientUid: String, messageData: UploadMessageDataEntity) : Flow<Boolean> {
        return dataRepository.sendFirstMessage(chatRoomId, senderUid, recipientUid, messageData)
    }
}