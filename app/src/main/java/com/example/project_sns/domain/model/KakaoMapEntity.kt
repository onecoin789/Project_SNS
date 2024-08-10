package com.example.project_sns.domain.model

data class KakaoMapEntity(
    val documents: List<KakaoDocumentsEntity>,
    val meta: KakaoMetaEntity
)

data class KakaoDocumentsEntity(

    val addressName: String,

    val categoryGroupCode: String,

    val categoryGroupName: String,

    val categoryName: String,

    val distance: String,

    val id: String,

    val phone: String,

    val placeName: String,

    val placeUrl: String,

    val roadAddressName: String,

    val lng: String,

    val lat: String,
)

data class KakaoMetaEntity(

    val isEnd: Boolean,

    val pageableCount: Int,

    val totalCount: Int,
)

