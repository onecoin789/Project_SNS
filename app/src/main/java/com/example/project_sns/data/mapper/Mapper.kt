package com.example.project_sns.data.mapper

import com.example.project_sns.data.response.CommentDataResponse
import com.example.project_sns.data.response.ImageDataResponse
import com.example.project_sns.data.response.KakaoDocumentsResponse
import com.example.project_sns.data.response.KakaoMapResponse
import com.example.project_sns.data.response.KakaoMetaResponse
import com.example.project_sns.data.response.MapDataResponse
import com.example.project_sns.data.response.PostDataResponse
import com.example.project_sns.data.response.ReCommentDataResponse
import com.example.project_sns.data.response.UserDataResponse
import com.example.project_sns.domain.model.CommentDataEntity
import com.example.project_sns.domain.model.ImageDataEntity
import com.example.project_sns.domain.model.KakaoDocumentsEntity
import com.example.project_sns.domain.model.KakaoMapEntity
import com.example.project_sns.domain.model.KakaoMetaEntity
import com.example.project_sns.domain.model.MapDataEntity
import com.example.project_sns.domain.model.PostDataEntity
import com.example.project_sns.domain.model.ReCommentDataEntity
import com.example.project_sns.domain.model.UserDataEntity

// <!---------- Firebase ---------->

fun UserDataResponse.toEntity() = UserDataEntity(
    uid = uid,
    name = name,
    email = email,
    profileImage = profileImage,
    createdAt = createdAt,
    intro = intro
)

fun PostDataResponse.toEntity() = PostDataEntity(
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

fun ImageDataResponse.toEntity() = ImageDataEntity(
    imageUri = imageUri,
    downloadUrl = downloadUrl,
    imageType = imageType
)

fun MapDataResponse.toEntity() = MapDataEntity(
    placeName = placeName, addressName = addressName, lat = lat, lng = lng
)

fun CommentDataResponse.toEntity() = CommentDataEntity(
    commentId = commentId,
    comment = comment,
    commentAt = commentAt,
    uid = uid,
    name = name,
    email = email,
    profileImage = profileImage,
    reCommentData = reCommentData?.toReCommentListEntity()
)

fun ReCommentDataResponse.toEntity() = ReCommentDataEntity(
    commentId = commentId,
    comment = comment,
    commentAt = commentAt,
    uid = uid,
    name = name,
    email = email,
    profileImage = profileImage
)

fun List<PostDataResponse>.toPostListEntity(): List<PostDataEntity> {
    return this.map { it.toEntity() }
}

fun List<CommentDataResponse>.toCommentListEntity(): List<CommentDataEntity> {
    return this.map { it.toEntity() }
}

fun List<ReCommentDataResponse>.toReCommentListEntity(): List<ReCommentDataEntity> {
    return this.map { it.toEntity() }
}




// <!---------- KakaoMap ---------->

fun KakaoMapResponse.toEntity() = KakaoMapEntity(
    meta = meta.toEntity(),
    documents = documents.map { it.toEntity() }
)

fun KakaoDocumentsResponse.toEntity() = KakaoDocumentsEntity(
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

fun KakaoMetaResponse.toEntity() = KakaoMetaEntity(
    isEnd = isEnd,
    pageableCount = pageableCount,
    totalCount = totalCount
)


