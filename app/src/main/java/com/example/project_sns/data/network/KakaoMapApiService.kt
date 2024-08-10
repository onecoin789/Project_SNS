package com.example.project_sns.data.network

import com.example.project_sns.BuildConfig
import com.example.project_sns.data.response.KakaoMapResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface KakaoMapApiService {

    @GET("keyword.json")
    suspend fun requestKakaoQuery(
        @Header("Authorization") apiKey: String = "KakaoAK"+" "+BuildConfig.API_KEY,
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): KakaoMapResponse
}