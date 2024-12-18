package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.entity.MessageDataEntity
import com.example.project_sns.domain.entity.UploadMessageDataEntity
import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(chatRoomId: String, token: String, recipientUser: String, accessToken: String, messageData: UploadMessageDataEntity): Flow<Boolean> {
        return dataRepository.sendMessage(
            chatRoomId = chatRoomId,
            token = token,
            recipientUser = recipientUser,
            accessToken = accessToken,
            messageData = messageData
        )
    }
}