package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.model.PostDataEntity
import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadPostUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(postData: PostDataEntity) : Flow<Boolean> {
        return dataRepository.uploadPost(postData)
    }
}