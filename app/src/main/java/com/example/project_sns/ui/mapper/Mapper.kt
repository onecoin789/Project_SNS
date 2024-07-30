package com.example.project_sns.ui.mapper

import com.example.project_sns.domain.model.CurrentUserEntity
import com.example.project_sns.ui.model.CurrentUserModel

fun CurrentUserEntity.toModel() = CurrentUserModel(
    name = name,
    email = email,
    profileImage = profileImage,
    createdAt = createdAt
)