package com.example.project_sns.data.di

import com.example.project_sns.data.repository.AuthRepositoryImpl
import com.example.project_sns.data.repository.DataRepositoryImpl
import com.example.project_sns.data.repository.KakaoMapRepositoryImpl
import com.example.project_sns.domain.repository.AuthRepository
import com.example.project_sns.domain.repository.DataRepository
import com.example.project_sns.domain.repository.KakaoMapRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindDataRepository(
        dataRepositoryImpl: DataRepositoryImpl
    ): DataRepository

    @Binds
    @Singleton
    abstract fun bindKakaoMapRepository(
        kakaoMapRepositoryImpl: KakaoMapRepositoryImpl
    ): KakaoMapRepository
}