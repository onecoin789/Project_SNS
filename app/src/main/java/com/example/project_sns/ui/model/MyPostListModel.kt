package com.example.project_sns.ui.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "MyPostList")
@Parcelize
data class MyPostListModel(
    //user data
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val profileImage: String?,
    val intro: String?,

    //post data
    val imageList: String,
    val postText: String?,
    val createdAt: String,
//    val mapData: MapDataModel?,
//    val likePost: List<String>
): Parcelable