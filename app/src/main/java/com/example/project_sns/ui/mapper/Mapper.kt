package com.example.project_sns.ui.mapper

import com.example.project_sns.domain.entity.ChatRoomEntity
import com.example.project_sns.domain.entity.CommentDataEntity
import com.example.project_sns.domain.entity.CommentEntity
import com.example.project_sns.domain.entity.FriendDataEntity
import com.example.project_sns.domain.entity.ImageDataEntity
import com.example.project_sns.domain.entity.KakaoDocumentsEntity
import com.example.project_sns.domain.entity.KakaoMapEntity
import com.example.project_sns.domain.entity.KakaoMetaEntity
import com.example.project_sns.domain.entity.MapDataEntity
import com.example.project_sns.domain.entity.MessageEntity
import com.example.project_sns.domain.entity.PostDataEntity
import com.example.project_sns.domain.entity.PostEntity
import com.example.project_sns.domain.entity.ReCommentDataEntity
import com.example.project_sns.domain.entity.ReCommentEntity
import com.example.project_sns.domain.entity.RequestDataEntity
import com.example.project_sns.domain.entity.RequestEntity
import com.example.project_sns.domain.entity.UserDataEntity
import com.example.project_sns.ui.model.ChatRoomModel
import com.example.project_sns.ui.util.PostImageType
import com.example.project_sns.ui.model.CommentDataModel
import com.example.project_sns.ui.model.CommentModel
import com.example.project_sns.ui.model.FriendDataModel
import com.example.project_sns.ui.model.ImageDataModel
import com.example.project_sns.ui.model.KakaoDocumentsModel
import com.example.project_sns.ui.model.KakaoMapModel
import com.example.project_sns.ui.model.KakaoMetaModel
import com.example.project_sns.ui.model.MapDataModel
import com.example.project_sns.ui.model.MessageModel
import com.example.project_sns.ui.model.PostDataModel
import com.example.project_sns.ui.model.PostModel
import com.example.project_sns.ui.model.ReCommentDataModel
import com.example.project_sns.ui.model.ReCommentModel
import com.example.project_sns.ui.model.RequestDataModel
import com.example.project_sns.ui.model.RequestModel
import com.example.project_sns.ui.model.UserDataModel
import com.example.project_sns.ui.model.toModel

// <!---------- Firebase ---------->

fun UserDataEntity.toModel() = UserDataModel(
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
    placeName = placeName,
    placeUrl = placeUrl,
    addressName = addressName,
    lat = lat,
    lng = lng
)


fun CommentDataEntity.toModel() = CommentDataModel(
    uid = uid,
    postId = postId,
    commentId = commentId,
    comment = comment,
    commentAt = commentAt,
    editedAt = editedAt,
    reCommentSize = reCommentSize
)

fun ReCommentDataEntity.toModel() = ReCommentDataModel(
    uid = uid,
    commentId = commentId,
    reCommentId = reCommentId,
    comment = comment,
    commentAt = commentAt,
    editedAt = editedAt
)

fun UserDataModel.toEntity() = UserDataEntity(
    uid = uid,
    name = name,
    email = email,
    profileImage = profileImage,
    createdAt = createdAt,
    intro = intro
)

fun PostDataModel.toEntity() = PostDataEntity(
    uid = uid,
    postId = postId,
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
    placeName = placeName,
    placeUrl = placeUrl,
    addressName = addressName,
    lat = lat,
    lng = lng
)

fun CommentDataModel.toEntity() = CommentDataEntity(
    uid = uid,
    postId = postId,
    commentId = commentId,
    comment = comment,
    commentAt = commentAt,
    editedAt = editedAt,
    reCommentSize = reCommentSize
)

fun ReCommentDataModel.toEntity() = ReCommentDataEntity(
    uid = uid,
    commentId = commentId,
    reCommentId = reCommentId,
    comment = comment,
    commentAt = commentAt,
    editedAt = editedAt
)

fun RequestDataEntity.toModel() = RequestDataModel(
    requestId = requestId,
    fromUid = fromUid,
    toUid = toUid
)


// <!---------- Multi Data ---------->

fun PostEntity.toModel() = PostModel(
    userData = userData.toModel(),
    postData = postData.toModel()
)

fun CommentEntity.toModel() = CommentModel(
    userData = userData.toModel(),
    commentData = commentData.toModel()
)

fun ReCommentEntity.toModel() = ReCommentModel(
    userData = userData.toModel(),
    reCommentData = reCommentData.toModel()
)

fun ReCommentModel.toEntity() = ReCommentEntity(
    userData = userData.toEntity(),
    reCommentData = reCommentData.toEntity()
)

fun PostModel.toEntity() = PostEntity(
    userData = userData.toEntity(),
    postData = postData.toEntity()
)

fun RequestEntity.toModel() = RequestModel(
    requestId = requestId,
    fromUid = fromUid.toModel(),
    toUid = toUid
)

fun FriendDataEntity.toModel() = FriendDataModel(
    friendList = friendList
)

fun ChatRoomEntity.toModel() = ChatRoomModel(
    userData = userData.toModel(),
    chatRoomData = chatRoomData.toModel()
)

fun MessageEntity.toModel() = MessageModel(
    userData = userData.toModel(),
    messageData = messageData.toModel()
)





fun List<PostDataEntity>.toPostListModel(): List<PostDataModel> {
    return this.map { it.toModel() }
}

fun List<CommentEntity>.toCommentListModel(): List<CommentModel> {
    return this.map { it.toModel() }
}

fun List<CommentDataModel>.toCommentListEntity(): List<CommentDataEntity> {
    return this.map { it.toEntity() }
}

fun List<RequestEntity>.toRequestDataModel(): List<RequestModel> {
    return this.map { it.toModel() }
}

fun List<ReCommentDataModel>.toReCommentDataListEntity(): List<ReCommentDataEntity> {
    return this.map { it.toEntity() }
}

fun List<ReCommentDataEntity>.toReCommentDataListModel(): List<ReCommentDataModel> {
    return this.map { it.toModel() }
}

fun List<PostEntity>.toPostDataListModel(): List<PostModel> {
    return this.map { it.toModel() }
}

fun List<ReCommentEntity>.toReCommentListModel(): List<ReCommentModel> {
    return this.map { it.toModel() }
}

fun List<ReCommentModel>.toReCommentListEntity(): List<ReCommentEntity> {
    return this.map { it.toEntity() }
}

fun List<FriendDataEntity>.toFriendListModel(): List<FriendDataModel> {
    return this.map { it.toModel() }
}

fun List<UserDataEntity>.toUserDataListModel(): List<UserDataModel> {
    return this.map { it.toModel() }
}

fun List<ChatRoomEntity>.toChatRoomListModel(): List<ChatRoomModel> {
    return this.map { it.toModel() }
}

fun List<MessageEntity>.toMessageListModel(): List<MessageModel> {
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