package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.entity.PostDataEntity
import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllPostUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(): Flow<List<PostDataEntity>> {
        return dataRepository.getAllPost()
    }
}