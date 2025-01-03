package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.entity.UserDataEntity
import com.example.project_sns.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserSearchResultUseCase @Inject constructor(private val dataRepository: DataRepository) {
    suspend operator fun invoke(query: String): Flow<List<UserDataEntity>> {
        return dataRepository.searchUserData(query = query)
    }
}