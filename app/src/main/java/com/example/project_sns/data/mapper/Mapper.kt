package com.example.project_sns.data.mapper

import com.example.project_sns.data.response.CommentDataResponse
import com.example.project_sns.data.response.CommentResponse
import com.example.project_sns.data.response.FriendDataResponse
import com.example.project_sns.data.response.ImageDataResponse
import com.example.project_sns.data.response.KakaoDocumentsResponse
import com.example.project_sns.data.response.KakaoMapResponse
import com.example.project_sns.data.response.KakaoMetaResponse
import com.example.project_sns.data.response.MapDataResponse
import com.example.project_sns.data.response.PostDataResponse
import com.example.project_sns.data.response.ReCommentDataResponse
import com.example.project_sns.data.response.ReCommentResponse
import com.example.project_sns.data.response.RequestDataResponse
import com.example.project_sns.data.response.RequestResponse
import com.example.project_sns.data.response.UserDataResponse
import com.example.project_sns.domain.model.CommentDataEntity
import com.example.project_sns.domain.model.CommentEntity
import com.example.project_sns.domain.model.FriendDataEntity
import com.example.project_sns.domain.model.ImageDataEntity
import com.example.project_sns.domain.model.KakaoDocumentsEntity
import com.example.project_sns.domain.model.KakaoMapEntity
import com.example.project_sns.domain.model.KakaoMetaEntity
import com.example.project_sns.domain.model.MapDataEntity
import com.example.project_sns.domain.model.PostDataEntity
import com.example.project_sns.domain.model.ReCommentDataEntity
import com.example.project_sns.domain.model.ReCommentEntity
import com.example.project_sns.domain.model.RequestDataEntity
import com.example.project_sns.domain.model.RequestEntity
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
    placeName = placeName,
    placeUrl = placeUrl,
    addressName = addressName,
    lat = lat,
    lng = lng
)

fun CommentDataResponse.toEntity() = CommentDataEntity(
    uid = uid,
    postId = postId,
    commentId = commentId,
    comment = comment,
    commentAt = commentAt,
    editedAt = editedAt,
    reCommentSize = reCommentSize
)

fun ReCommentDataResponse.toEntity() = ReCommentDataEntity(
    uid = uid,
    commentId = commentId,
    reCommentId = reCommentId,
    comment = comment,
    commentAt = commentAt,
    editedAt = editedAt
)

fun RequestDataResponse.toEntity() = RequestDataEntity(
    requestId = requestId,
    fromUid = fromUid,
    toUid = toUid
)

// <!---------- Multi Data ---------->

fun CommentResponse.toEntity() = CommentEntity(
    userData = userData.toEntity(),
    commentData = commentData.toEntity()
)

fun ReCommentResponse.toEntity() = ReCommentEntity(
    userData = userData.toEntity(),
    reCommentData = reCommentData.toEntity()
)

fun RequestResponse.toEntity() = RequestEntity(
    requestId = requestId,
    fromUid = fromUid.toEntity(),
    toUid = toUid
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

fun List<CommentResponse>.toCommentEntity(): List<CommentEntity> {
    return this.map { it.toEntity() }
}

fun List<RequestDataResponse>.toRequestDataEntity(): List<RequestDataEntity> {
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


