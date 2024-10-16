package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.entity.ChatRoomDataEntity
import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckChatRoomDataUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(senderUid: String, recipientUid: String): Flow<ChatRoomDataEntity?> {
        return dataRepository.checkChatRoom(senderUid, recipientUid)
    }
}