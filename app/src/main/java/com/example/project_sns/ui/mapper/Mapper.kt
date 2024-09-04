package com.example.project_sns.ui.mapper

import com.example.project_sns.domain.model.CommentDataEntity
import com.example.project_sns.domain.model.CurrentUserEntity
import com.example.project_sns.domain.model.ImageDataEntity
import com.example.project_sns.domain.model.KakaoDocumentsEntity
import com.example.project_sns.domain.model.KakaoMapEntity
import com.example.project_sns.domain.model.KakaoMetaEntity
import com.example.project_sns.domain.model.MapDataEntity
import com.example.project_sns.domain.model.PostDataEntity
import com.example.project_sns.domain.model.ReCommentDataEntity
import com.example.project_sns.ui.CurrentUserModel
import com.example.project_sns.ui.view.main.profile.PostImageType
import com.example.project_sns.ui.view.model.CommentDataModel
import com.example.project_sns.ui.view.model.ImageDataModel
import com.example.project_sns.ui.view.model.KakaoDocumentsModel
import com.example.project_sns.ui.view.model.KakaoMapModel
import com.example.project_sns.ui.view.model.KakaoMetaModel
import com.example.project_sns.ui.view.model.MapDataModel
import com.example.project_sns.ui.view.model.PostDataModel
import com.example.project_sns.ui.view.model.ReCommentDataModel

// <!---------- Firebase ---------->

fun CurrentUserEntity.toModel() = CurrentUserModel(
    uid = uid,
    name = name,
    email = email,
    profileImage = profileImage,
    createdAt = createdAt,
    intro = intro
)

fun PostDataEntity.toModel() = PostDataModel(
    uid = uid,
    postId = postId,
    profileImage = profileImage,
    name = name,
    email = email,
    imageList = imageList?.map { it.toModel() },
    postText = postText,
    createdAt = createdAt,
    editedAt = editedAt,
    mapData = mapData?.toModel()
)

fun ImageDataEntity.toModel() = ImageDataModel(
    imageUri = imageUri,
    downloadUrl = downloadUrl,
    imageType = imageType
)

fun ImageDataModel.toViewType(type: String): PostImageType {
    return if (this.imageType == type) {
        PostImageType.PostVideo(this)
    } else {
        PostImageType.PostImage(this)
    }
}

fun MapDataEntity.toModel() = MapDataModel(
    placeName = placeName, addressName = addressName, lat = lat, lng = lng
)


fun CommentDataEntity.toModel() = CommentDataModel(
    commentId = commentId,
    comment = comment,
    commentAt = commentAt,
    uid = uid,
    name = name,
    email = email,
    profileImage = profileImage,
    reCommentData = reCommentData?.toReCommentListModel()
)

fun ReCommentDataEntity.toModel() = ReCommentDataModel(
    commentId = commentId,
    comment = comment,
    commentAt = commentAt,
    uid = uid,
    name = name,
    email = email,
    profileImage = profileImage
)

fun PostDataModel.toEntity() = PostDataEntity(
    uid = uid,
    postId = postId,
    profileImage = profileImage,
    name = name,
    email = email,
    imageList = imageList?.map { it.toEntity() },
    postText = postText,
    createdAt = createdAt,
    editedAt = editedAt,
    mapData = mapData?.toEntity()
)

fun ImageDataModel.toEntity() = ImageDataEntity(
    imageUri = imageUri, downloadUrl = downloadUrl, imageType = imageType
)

fun MapDataModel.toEntity() = MapDataEntity(
    placeName = placeName, addressName = addressName, lat = lat, lng = lng
)

fun CommentDataModel.toEntity() = CommentDataEntity(
    commentId = commentId,
    comment = comment,
    commentAt = commentAt,
    uid = uid,
    name = name,
    email = email,
    profileImage = profileImage,
    reCommentData = reCommentData?.toReCommentListEntity()
)

fun ReCommentDataModel.toEntity() = ReCommentDataEntity(
    commentId = commentId,
    comment = comment,
    commentAt = commentAt,
    uid = uid,
    name = name,
    email = email,
    profileImage = profileImage
)

fun List<PostDataEntity>.toPostListModel(): List<PostDataModel> {
    return this.map { it.toModel() }
}

fun List<CommentDataEntity>.toCommentListModel(): List<CommentDataModel> {
    return this.map { it.toModel() }
}

fun List<CommentDataModel>.toCommentListEntity(): List<CommentDataEntity> {
    return this.map { it.toEntity() }
}

fun List<ReCommentDataModel>.toReCommentListEntity(): List<ReCommentDataEntity> {
    return this.map { it.toEntity() }
}

fun List<ReCommentDataEntity>.toReCommentListModel(): List<ReCommentDataModel> {
    return this.map { it.toModel() }
}



// <!---------- KakaoMap ---------->

fun KakaoMapEntity.toModel() = KakaoMapModel(
    meta = meta.toModel(),
    documents = documents.map { it.toModel() }
)

fun KakaoDocumentsEntity.toModel() = KakaoDocumentsModel(
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

fun KakaoMetaEntity.toModel() = KakaoMetaModel(
    isEnd = isEnd,
    pageableCount = pageableCount,
    totalCount = totalCount
)

fun List<KakaoDocumentsEntity>.toKakaoListEntity(): List<KakaoDocumentsModel> {
    return this.map { it.toModel() }
}