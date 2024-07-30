package com.example.project_sns.data.mapper

import com.example.project_sns.data.response.CurrentUserResponse
import com.example.project_sns.domain.model.CurrentUserEntity



fun List<CurrentUserResponse>.toEntity(): List<CurrentUserEntity> {
    return this.map { it.toEntity() }
}

fun CurrentUserResponse.toEntity() = CurrentUserEntity(
    name = name,
    email = email,
    profileImage = profileImage,
    createdAt = createdAt
)