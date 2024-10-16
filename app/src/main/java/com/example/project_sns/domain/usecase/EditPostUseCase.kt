package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.entity.PostDataEntity
import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EditPostUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(postData: PostDataEntity?): Flow<Boolean> {
        return dataRepository.editPost(postData)
    }
}