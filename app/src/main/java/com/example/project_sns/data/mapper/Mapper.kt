package com.example.project_sns.data.mapper

import com.example.project_sns.data.response.CurrentUserResponse
import com.example.project_sns.data.response.KakaoDocumentsResponse
import com.example.project_sns.data.response.KakaoMapResponse
import com.example.project_sns.data.response.KakaoMetaResponse
import com.example.project_sns.data.response.PostDataResponse
import com.example.project_sns.domain.model.CurrentUserEntity
import com.example.project_sns.domain.model.KakaoDocumentsEntity
import com.example.project_sns.domain.model.KakaoMapEntity
import com.example.project_sns.domain.model.KakaoMetaEntity
import com.example.project_sns.domain.model.PostDataEntity

// <!---------- Firebase ---------->

fun CurrentUserResponse.toEntity() = CurrentUserEntity(
    uid = uid,
    name = name,
    email = email,
    profileImage = profileImage,
    createdAt = createdAt,
    intro = intro
)

fun PostDataResponse.toEntity() = PostDataEntity(
    postId = postId,
    profileImage = profileImage,
    name = name,
    email = email,
    image = image,
    postText = postText,
    lat = lat,
    lng = lng,
    createdAt = createdAt
)

fun List<PostDataResponse>.toListEntity(): List<PostDataEntity> {
    return this.map { it.toEntity() }
}


// <!---------- KakaoMap ---------->

fun KakaoMapResponse.toEntity() = KakaoMapEntity(
    meta = meta.toEntity(),
    documents = documents.map { it.toEntity() }
)

fun KakaoDocumentsResponse.toEntity() = KakaoDocumentsEntity(
    addressName = addressName,
    categoryGroupCode = categoryGroupCode,
    categoryGroupName = categoryGroupName,
    categoryName = categoryName,
    distance = distance,
    id = id,
    phone = phone,
    placeName = placeName,
    placeUrl = placeUrl,
    roadAddressName = roadAddressName,
    lng = lng,
    lat = lat
)

fun KakaoMetaResponse.toEntity() = KakaoMetaEntity(
    isEnd = isEnd,
    pageableCount = pageableCount,
    totalCount = totalCount
)


