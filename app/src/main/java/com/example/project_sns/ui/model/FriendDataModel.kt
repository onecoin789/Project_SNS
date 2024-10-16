package com.example.project_sns.ui.model

import com.example.project_sns.domain.entity.FriendDataEntity

data class FriendDataModel(
    val friendList: List<String>
)

fun FriendDataEntity.toModel() = FriendDataModel(
    friendList = friendList
)