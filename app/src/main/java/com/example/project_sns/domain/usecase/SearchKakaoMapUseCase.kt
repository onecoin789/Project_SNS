package com.example.project_sns.domain.usecase

import com.example.project_sns.domain.entity.KakaoMapEntity
import com.example.project_sns.domain.repository.KakaoMapRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchKakaoMapUseCase @Inject constructor(private val kakaoMapRepository: KakaoMapRepository) {
    suspend operator fun invoke(query: String, size: Int, page: Int): Flow<KakaoMapEntity?> {
        return kakaoMapRepository.getMapDataList(query, page, size)
    }
}