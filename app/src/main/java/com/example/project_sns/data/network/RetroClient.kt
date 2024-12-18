package com.example.project_sns.data.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetroClient {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KaKaoMapRetroClient

//    @Qualifier
//    @Retention(AnnotationRetention.BINARY)
//    annotation class FCMRetroClient

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient
            .Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .addNetworkInterceptor(loggingInterceptor)
            .build()
    }


    //kakao map retrofit
    private const val KAKAO_MAP_URL = "https://dapi.kakao.com/v2/local/search/"

    @Singleton
    @Provides
    @KaKaoMapRetroClient
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(KAKAO_MAP_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    @KaKaoMapRetroClient
    fun kakaoMapApiService(@KaKaoMapRetroClient retrofit: Retrofit): KakaoMapApiService {
        return retrofit.create(KakaoMapApiService::class.java)
    }



//    //fcm retrofit
//    private const val FCM_URL = "https://fcm.googleapis.com/v1/projects/project-sns-58aea/messages:/"
//
//    @Singleton
//    @Provides
//    @FCMRetroClient
//    fun provideFCMRetrofit(client: OkHttpClient): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(FCM_URL)
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    @Singleton
//    @Provides
//    @FCMRetroClient
//    fun fcmApiService(@FCMRetroClient retrofit: Retrofit): FCMApiService {
//        return retrofit.create(FCMApiService::class.java)
//    }

}