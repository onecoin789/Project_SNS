package com.example.project_sns.domain.repository

import com.example.project_sns.domain.model.KakaoMapEntity
import kotlinx.coroutines.flow.Flow

interface KakaoMapRepository {

    suspend fun getMapDataList(query: String, page: Int, size: Int) : Flow<KakaoMapEntity?>

}