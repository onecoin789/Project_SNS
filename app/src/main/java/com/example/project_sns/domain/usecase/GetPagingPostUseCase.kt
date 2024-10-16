package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.entity.PostEntity
import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPagingPostUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(lastVisibleItem: Flow<Int>): Flow<List<PostEntity>?> {
        return dataRepository.getPagingPost(lastVisibleItem)
    }
}