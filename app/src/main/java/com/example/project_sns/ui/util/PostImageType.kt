package com.example.project_sns.ui.util

import com.example.project_sns.ui.view.model.ImageDataModel


sealed class PostImageType {
    data class PostImage(val type: ImageDataModel): PostImageType()
    data class PostVideo(val type: ImageDataModel): PostImageType()
}

fun PostImageType.getType(): String? {
    return when(this) {
        is PostImageType.PostImage -> this.type.imageType
        is PostImageType.PostVideo -> this.type.imageType
    }
}
