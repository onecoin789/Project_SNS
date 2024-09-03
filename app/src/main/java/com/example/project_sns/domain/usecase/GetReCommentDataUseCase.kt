package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.model.ReCommentDataEntity
import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReCommentDataUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(postId: String, commentId: String): Flow<List<ReCommentDataEntity>> {
        return dataRepository.getReComment(postId, commentId)
    }
}