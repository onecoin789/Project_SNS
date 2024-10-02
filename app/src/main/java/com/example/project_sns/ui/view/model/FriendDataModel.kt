package com.example.project_sns.ui.view.model

import com.example.project_sns.domain.model.FriendDataEntity

data class FriendDataModel(
    val friendList: List<String>
)

fun FriendDataEntity.toModel() = FriendDataModel(
    friendList = friendList
)