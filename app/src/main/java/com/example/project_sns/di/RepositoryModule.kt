package com.example.project_sns.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.project_sns.data.repository.AuthRepositoryImpl
import com.example.project_sns.data.repository.DataRepositoryImpl
import com.example.project_sns.data.repository.KakaoMapRepositoryImpl
import com.example.project_sns.domain.repository.AuthRepository
import com.example.project_sns.domain.repository.DataRepository
import com.example.project_sns.domain.repository.KakaoMapRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("LOGIN_CHECK_DATASTORE")


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

    companion object {
        @Provides
        @Singleton
        fun provideDataStorePreferences(@ApplicationContext applicationContext: Context): DataStore<Preferences> {
            return applicationContext.dataStore
        }
    }
}