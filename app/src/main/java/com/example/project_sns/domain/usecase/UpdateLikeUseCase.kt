package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateLikeUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(postId: String, likeValue: Boolean): Flow<Boolean> {
        return dataRepository.updateLike(postId, likeValue)
    }
}