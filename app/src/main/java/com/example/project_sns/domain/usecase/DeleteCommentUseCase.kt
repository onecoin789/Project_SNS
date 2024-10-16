package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.repository.DataRepository
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(postId: String): Result<String> {
        return dataRepository.deleteComment(postId)
    }
}