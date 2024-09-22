package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.model.CommentDataEntity
import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadCommentUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(commentData: CommentDataEntity?): Flow<Boolean> {
        return dataRepository.uploadComment(commentData)
    }
}