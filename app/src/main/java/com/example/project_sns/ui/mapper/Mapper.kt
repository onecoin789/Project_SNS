package com.example.project_sns.ui.mapper

import com.example.project_sns.domain.model.CommentDataEntity
import com.example.project_sns.domain.model.CurrentUserEntity
import com.example.project_sns.domain.model.ImageDataEntity
import com.example.project_sns.domain.model.KakaoDocumentsEntity
import com.example.project_sns.domain.model.KakaoMapEntity
import com.example.project_sns.domain.model.KakaoMetaEntity
import com.example.project_sns.domain.model.MapDataEntity
import com.example.project_sns.domain.model.PostDataEntity
import com.example.project_sns.ui.CurrentUserModel
import com.example.project_sns.ui.view.main.profile.PostImageType
import com.example.project_sns.ui.view.model.CommentDataModel
import com.example.project_sns.ui.view.model.ImageDataModel
import com.example.project_sns.ui.view.model.KakaoDocumentsModel
import com.example.project_sns.ui.view.model.KakaoMapModel
import com.example.project_sns.ui.view.model.KakaoMetaModel
import com.example.project_sns.ui.view.model.MapDataModel
import com.example.project_sns.ui.view.model.PostDataModel

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
    mapData = mapData?.toModel(),
    commentData = commentData?.toModel()
)

fun ImageDataEntity.toModel() = ImageDataModel(
    imageUri = imageUri, imageType = imageType
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
    commenterProfile = commenterProfile,
    commenterEmail = commenterEmail,
    commenterName = commenterName,
    comment = comment
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
    mapData = mapData?.toEntity(),
    commentData = commentData?.toEntity()
)

fun ImageDataModel.toEntity() = ImageDataEntity(
    imageUri = imageUri, imageType = imageType
)

fun MapDataModel.toEntity() = MapDataEntity(
    placeName = placeName, addressName = addressName, lat = lat, lng = lng
)

fun CommentDataModel.toEntity() = CommentDataEntity(
    commenterProfile = commenterProfile,
    commenterEmail = commenterEmail,
    commenterName = commenterName,
    comment = comment
)

fun List<PostDataEntity>.toListModel(): List<PostDataModel> {
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