package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.repository.DataRepository
import javax.inject.Inject

class DeleteReCommentUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(commentId: String, reCommentId: String): Result<String> {
        return dataRepository.deleteReComment(commentId, reCommentId)
    }
}