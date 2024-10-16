package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.entity.CommentDataEntity
import com.example.project_sns.domain.entity.PostDataEntity
import com.example.project_sns.domain.repository.DataRepository
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(postData: PostDataEntity?, commentList: List<CommentDataEntity>?): Result<String> {
        return dataRepository.deletePost(postData, commentList)
    }
}