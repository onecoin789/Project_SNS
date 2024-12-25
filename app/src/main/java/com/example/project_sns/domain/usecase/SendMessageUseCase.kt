package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.entity.UploadMessageDataEntity
import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(chatRoomId: String, token: String, sendUser: String, accessToken: String, recipientUid: String, messageData: UploadMessageDataEntity): Flow<Boolean> {
        return dataRepository.sendMessage(
            chatRoomId = chatRoomId,
            token = token,
            sendUser = sendUser,
            accessToken = accessToken,
            recipientUid = recipientUid,
            messageData = messageData
        )
    }
}