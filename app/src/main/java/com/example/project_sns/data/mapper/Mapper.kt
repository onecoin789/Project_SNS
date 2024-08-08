package com.example.project_sns.data.mapper

import com.example.project_sns.data.response.CurrentUserResponse
import com.example.project_sns.data.response.PostDataResponse
import com.example.project_sns.domain.model.CurrentUserEntity
import com.example.project_sns.domain.model.PostDataEntity


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

fun List<PostDataResponse>.toListEntity() : List<PostDataEntity> {
    return this.map { it.toEntity() }
}

