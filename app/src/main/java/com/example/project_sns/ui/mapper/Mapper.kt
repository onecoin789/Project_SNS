package com.example.project_sns.ui.mapper

import com.example.project_sns.domain.model.CommentDataEntity
import com.example.project_sns.domain.model.CommentEntity
import com.example.project_sns.domain.model.ImageDataEntity
import com.example.project_sns.domain.model.KakaoDocumentsEntity
import com.example.project_sns.domain.model.KakaoMapEntity
import com.example.project_sns.domain.model.KakaoMetaEntity
import com.example.project_sns.domain.model.MapDataEntity
import com.example.project_sns.domain.model.PostDataEntity
import com.example.project_sns.domain.model.PostEntity
import com.example.project_sns.domain.model.ReCommentDataEntity
import com.example.project_sns.domain.model.ReCommentEntity
import com.example.project_sns.domain.model.RequestDataEntity
import com.example.project_sns.domain.model.RequestEntity
import com.example.project_sns.domain.model.UserDataEntity
import com.example.project_sns.ui.util.PostImageType
import com.example.project_sns.ui.view.model.CommentDataModel
import com.example.project_sns.ui.view.model.CommentModel
import com.example.project_sns.ui.view.model.ImageDataModel
import com.example.project_sns.ui.view.model.KakaoDocumentsModel
import com.example.project_sns.ui.view.model.KakaoMapModel
import com.example.project_sns.ui.view.model.KakaoMetaModel
import com.example.project_sns.ui.view.model.MapDataModel
import com.example.project_sns.ui.view.model.PostDataModel
import com.example.project_sns.ui.view.model.PostModel
import com.example.project_sns.ui.view.model.ReCommentDataModel
import com.example.project_sns.ui.view.model.ReCommentModel
import com.example.project_sns.ui.view.model.RequestDataModel
import com.example.project_sns.ui.view.model.RequestModel
import com.example.project_sns.ui.view.model.UserDataModel

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
    placeName = placeName, addressName = addressName, lat = lat, lng = lng
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
    placeName = placeName, addressName = addressName, lat = lat, lng = lng
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
    fromUid = fromUid.toModel(),
    toUid = toUid
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