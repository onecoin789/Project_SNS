package com.example.project_sns.data.mapper

import com.example.project_sns.data.response.ChatRoomResponse
import com.example.project_sns.data.response.CommentDataResponse
import com.example.project_sns.data.response.CommentResponse
import com.example.project_sns.data.response.ImageDataResponse
import com.example.project_sns.data.response.KakaoDocumentsResponse
import com.example.project_sns.data.response.KakaoMapResponse
import com.example.project_sns.data.response.KakaoMetaResponse
import com.example.project_sns.data.response.MapDataResponse
import com.example.project_sns.data.response.MessageResponse
import com.example.project_sns.data.response.PostDataResponse
import com.example.project_sns.data.response.ReCommentDataResponse
import com.example.project_sns.data.response.ReCommentResponse
import com.example.project_sns.data.response.RequestDataResponse
import com.example.project_sns.data.response.RequestResponse
import com.example.project_sns.data.response.UserDataResponse
import com.example.project_sns.data.response.toEntity
import com.example.project_sns.domain.entity.ChatRoomEntity
import com.example.project_sns.domain.entity.CommentDataEntity
import com.example.project_sns.domain.entity.CommentEntity
import com.example.project_sns.domain.entity.ImageDataEntity
import com.example.project_sns.domain.entity.KakaoDocumentsEntity
import com.example.project_sns.domain.entity.KakaoMapEntity
import com.example.project_sns.domain.entity.KakaoMetaEntity
import com.example.project_sns.domain.entity.MapDataEntity
import com.example.project_sns.domain.entity.MessageEntity
import com.example.project_sns.domain.entity.PostDataEntity
import com.example.project_sns.domain.entity.ReCommentDataEntity
import com.example.project_sns.domain.entity.ReCommentEntity
import com.example.project_sns.domain.entity.RequestDataEntity
import com.example.project_sns.domain.entity.RequestEntity
import com.example.project_sns.domain.entity.UserDataEntity

// <!---------- Firebase ---------->

fun UserDataResponse.toEntity() = UserDataEntity(
    uid = uid,
    name = name,
    email = email,
    profileImage = profileImage,
    createdAt = createdAt,
    intro = intro,
    token = token
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

fun ChatRoomResponse.toEntity() = ChatRoomEntity(
    userData = userData.toEntity(),
    chatRoomData = chatRoomData.toEntity()
)

fun MessageResponse.toEntity() = MessageEntity(
    userData = userData.toEntity(),
    messageData = messageData.toEntity()
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

fun List<ChatRoomResponse>.toChatRoomListEntity(): List<ChatRoomEntity> {
    return this.map { it.toEntity() }
}

fun List<MessageResponse>.toMessageListEntity(): List<MessageEntity> {
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


