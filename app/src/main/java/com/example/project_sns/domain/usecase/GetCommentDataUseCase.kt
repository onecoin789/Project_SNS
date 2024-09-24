package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.model.CommentEntity
import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCommentDataUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(postId: String, lastVisibleItem: Flow<Int>): Flow<List<CommentEntity>> {
        return dataRepository.getComment(postId, lastVisibleItem)
    }
}