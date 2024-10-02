package com.example.project_sns.data.response

import com.example.project_sns.domain.model.FriendDataEntity

data class FriendDataResponse (
    val friendList: List<String> = emptyList()
)

fun FriendDataResponse.toEntity() = FriendDataEntity(
    friendList = friendList
)