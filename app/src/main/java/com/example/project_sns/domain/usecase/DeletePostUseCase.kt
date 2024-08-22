package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.model.PostDataEntity
import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(postData: PostDataEntity?): Result<String> {
        return dataRepository.deletePost(postData)
    }
}