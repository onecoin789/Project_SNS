package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.model.ReCommentDataEntity
import com.example.project_sns.domain.repository.DataRepository
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(postId: String, commentId: String, reCommentList: List<ReCommentDataEntity>?): Result<String> {
        return dataRepository.deleteComment(postId, commentId, reCommentList)
    }
}