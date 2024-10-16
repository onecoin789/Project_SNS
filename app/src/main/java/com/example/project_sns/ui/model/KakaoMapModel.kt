package com.example.project_sns.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class KakaoMapModel(
    val documents: List<KakaoDocumentsModel>,
    val meta: KakaoMetaModel
)

@Parcelize
data class KakaoDocumentsModel(

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
): Parcelable

data class KakaoMetaModel(

    val isEnd: Boolean,
    val pageableCount: Int,
    val totalCount: Int,
)