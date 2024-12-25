package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.entity.UploadMessageDataEntity
import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendFirstMessageUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(chatRoomId: String, token: String, sendUser: String, accessToken: String, senderUid: String, recipientUid: String, messageData: UploadMessageDataEntity) : Flow<Boolean> {
        return dataRepository.sendFirstMessage(
            chatRoomId = chatRoomId,
            token = token,
            sendUser = sendUser,
            accessToken = accessToken,
            senderUid = senderUid,
            recipientUid = recipientUid,
            messageData = messageData
        )
    }
}