package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.model.ReCommentDataEntity
import com.example.project_sns.domain.repository.DataRepository
import com.example.project_sns.ui.view.model.ReCommentDataModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadReCommentUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(postId: String, commentId: String, reCommentData: ReCommentDataEntity?): Flow<Boolean> {
        return dataRepository.uploadReComment(postId, commentId, reCommentData)
    }
}