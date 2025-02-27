package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.entity.ReCommentEntity
import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReCommentDataUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(commentId: String, lastVisibleItem: Flow<Int>): Flow<List<ReCommentEntity>> {
        return dataRepository.getReComment(commentId, lastVisibleItem)
    }
}