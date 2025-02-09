package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckReCommentChangeUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(commentId: String): Flow<Boolean> {
        return dataRepository.checkReCommentChange(commentId)
    }
}