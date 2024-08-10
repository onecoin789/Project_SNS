package com.example.project_sns.data.repository

import com.example.project_sns.data.mapper.toEntity
import com.example.project_sns.data.network.KakaoMapApiService
import com.example.project_sns.domain.model.KakaoMapEntity
import com.example.project_sns.domain.repository.KakaoMapRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class KakaoMapRepositoryImpl @Inject constructor(
    private val kakaoMapApiService: KakaoMapApiService
) : KakaoMapRepository {
    override suspend fun getMapDataList(query: String, page: Int, size: Int): Flow<KakaoMapEntity?> {
        return flow {
            emit(kakaoMapApiService.requestKakaoQuery(query = query, page = page, size = size).toEntity())
        }
    }
}