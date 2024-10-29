package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.entity.ChatRoomEntity
import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatRoomListUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(): Flow<List<ChatRoomEntity>> {
        return dataRepository.getChatRoomList()
    }
}