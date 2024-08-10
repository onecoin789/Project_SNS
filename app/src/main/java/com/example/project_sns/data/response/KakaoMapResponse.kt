package com.example.project_sns.data.response

import com.google.gson.annotations.SerializedName

data class KakaoMapResponse(
    val documents: List<KakaoDocumentsResponse>,
    val meta: KakaoMetaResponse
)

data class KakaoDocumentsResponse(
    // "대구 북구 동천동 903-1
    @SerializedName("address_name")
    val addressName: String,

    // "FD6"
    @SerializedName("category_group_code")
    val categoryGroupCode: String,

    // "음식점"
    @SerializedName("category_group_name")
    val categoryGroupName: String,

    // "음식점 > 한식 > 육류,고기"
    @SerializedName("category_name")
    val categoryName: String,

    // ""
    @SerializedName("distance")
    val distance: String,

    // "1528282879"
    @SerializedName("id")
    val id: String,

    // "053-323-5933"
    @SerializedName("phone")
    val phone: String,

    // "고기굽는남자 칠곡3지구점"
    @SerializedName("place_name")
    val placeName: String,

    // "http://place.map.kakao.com/1528282879"
    @SerializedName("place_url")
    val placeUrl: String,

    // "대구 북구 동천로 135-13"
    @SerializedName("road_address_name")
    val roadAddressName: String,

    // "128.55919532987204"
    @SerializedName("x")
    val lng: String,

    // "35.943758453936034"
    @SerializedName("y")
    val lat: String,
)

data class KakaoMetaResponse(
    // 현재 페이지가 마지막 페이지인지 여부
    // 값이 false면 다음 요청 시 page 값을 증가시켜 다음 페이지 요청 가능
    @SerializedName("is_end")
    val isEnd: Boolean,

    // "total_count 중 노출 가능 문서 수"
    @SerializedName("pageable_count")
    val pageableCount: Int,

    // "35.943758453936034"
    @SerializedName("total_count")
    val totalCount: Int,
)