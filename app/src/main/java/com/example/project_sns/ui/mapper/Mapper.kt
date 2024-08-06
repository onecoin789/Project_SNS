package com.example.project_sns.ui.mapper

import com.example.project_sns.domain.model.CurrentUserEntity
import com.example.project_sns.domain.model.PostDataEntity
import com.example.project_sns.ui.CurrentUserModel
import com.example.project_sns.ui.view.model.PostDataModel

fun CurrentUserEntity.toModel() = CurrentUserModel(
    uid = uid,
    name = name,
    email = email,
    profileImage = profileImage,
    createdAt = createdAt
)

fun PostDataEntity.toModel() = PostDataModel(
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

fun PostDataModel.toEntity() = PostDataEntity(
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

fun List<PostDataEntity>.toListModel() : List<PostDataModel> {
    return this.map { it.toModel() }
}