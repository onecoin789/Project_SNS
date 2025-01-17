package com.example.project_sns.data.repository

import android.util.Log
import androidx.core.net.toUri
import com.example.project_sns.FirebaseMessagingService
import com.example.project_sns.data.mapper.toCommentListEntity
import com.example.project_sns.data.mapper.toEntity
import com.example.project_sns.data.mapper.toPostListEntity
import com.example.project_sns.data.mapper.toReCommentListEntity
import com.example.project_sns.data.mapper.toUserListEntity
import com.example.project_sns.data.response.ChatRoomDataResponse
import com.example.project_sns.data.response.ChatRoomResponse
import com.example.project_sns.data.response.CommentDataResponse
import com.example.project_sns.data.response.MessageDataResponse
import com.example.project_sns.data.response.PostDataResponse
import com.example.project_sns.data.response.PostResponse
import com.example.project_sns.data.response.ReCommentDataResponse
import com.example.project_sns.data.response.UserDataResponse
import com.example.project_sns.data.response.toChatRoomListEntity
import com.example.project_sns.data.response.toEntity
import com.example.project_sns.data.response.toMessageListEntity
import com.example.project_sns.domain.entity.ChatRoomDataEntity
import com.example.project_sns.domain.entity.ChatRoomEntity
import com.example.project_sns.domain.entity.CommentDataEntity
import com.example.project_sns.domain.entity.CommentEntity
import com.example.project_sns.domain.entity.ImageDataEntity
import com.example.project_sns.domain.entity.MessageEntity
import com.example.project_sns.domain.entity.PostDataEntity
import com.example.project_sns.domain.entity.PostEntity
import com.example.project_sns.domain.entity.ReCommentDataEntity
import com.example.project_sns.domain.entity.ReCommentEntity
import com.example.project_sns.domain.entity.UploadMessageDataEntity
import com.example.project_sns.domain.entity.UserDataEntity
import com.example.project_sns.domain.repository.DataRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val COLLECTION_USER = "user"
private const val COLLECTION_POST = "post"
private const val COLLECTION_COMMENT = "comment"
private const val COLLECTION_RE_COMMENT = "reComment"
private const val COLLECTION_CHAT = "chat"
private const val COLLECTION_CHAT_MESSAGE = "message"

private const val TAG = "DataRepositoryImpl"

class DataRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : DataRepository {
    override suspend fun uploadPost(postData: PostDataEntity): Flow<Boolean> {
        return flow {
            try {
                val currentUser = auth.currentUser?.uid
                val imageList: ArrayList<ImageDataEntity> = arrayListOf()
                if (currentUser != null) {
                    val data = hashMapOf(
                        "postId" to postData.postId,
                        "uid" to currentUser,
                        "postText" to postData.postText,
                        "mapData" to postData.mapData,
                        "createdAt" to postData.createdAt,
                        "editedAt" to postData.editedAt,
                        "likePost" to postData.likePost
                    )
                    db.collection(COLLECTION_POST).document(postData.postId).set(data).await()
                    if (postData.imageList != null) {
                        for (i in 0 until postData.imageList.count()) {
                            val imageToUri = postData.imageList[i].imageUri.toUri()
                            val storageRef =
                                storage.getReference("image")
                                    .child("${currentUser}/${postData.postId}/${postData.createdAt}_${i}")
                            if (imageToUri.pathSegments?.contains("video") == true) {
                                storageRef.putFile(imageToUri).addOnSuccessListener {
                                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                        imageList.add(
                                            ImageDataEntity(
                                                downloadUrl.toString(),
                                                imageToUri.toString(),
                                                "video"
                                            )
                                        )
                                        val imageData =
                                            mapOf("imageList" to imageList.sortedBy { it.downloadUrl })
                                        db.collection(COLLECTION_POST).document(postData.postId)
                                            .update(imageData)
                                    }
                                }
                            } else {
                                storageRef.putFile(imageToUri).addOnSuccessListener {
                                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                        imageList.add(
                                            ImageDataEntity(
                                                downloadUrl.toString(),
                                                imageToUri.toString(),
                                                "image"
                                            )
                                        )
                                        val imageData =
                                            mapOf("imageList" to imageList.sortedBy { it.downloadUrl })
                                        db.collection(COLLECTION_POST).document(postData.postId)
                                            .update(imageData)
                                    }
                                }
                            }
                        }
                    }
                    emit(true)
                }
            } catch (e: Exception) {
                emit(false)
            }
        }
    }

    override suspend fun getCurrentUserPost(uid: String): Flow<List<PostDataEntity>> {
        return callbackFlow {
            val mAuth = auth.currentUser?.uid
            if (mAuth != null) {
                val postData = db.collection(COLLECTION_POST).whereEqualTo("uid", uid)
                val snapShotListener = postData.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(emptyList()).isSuccess
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val postResponse = snapshot.toObjects(PostDataResponse::class.java)
                        trySend(postResponse.toPostListEntity()).isSuccess
                    } else {
                        trySend(emptyList()).isSuccess
                    }
                }
                awaitClose {
                    snapShotListener.remove()
                }
            }
        }
    }

    override suspend fun getAllPost(): Flow<List<PostDataEntity>> {
        return callbackFlow {
            val snapshotListener =
                db.collection(COLLECTION_POST).orderBy("createdAt", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(emptyList()).isSuccess
                            return@addSnapshotListener
                        }
                        if (snapshot != null && !snapshot.isEmpty) {
                            val postResponse = snapshot.toObjects(PostDataResponse::class.java)
                            trySend(postResponse.toPostListEntity()).isSuccess

                        } else {
                            trySend(emptyList()).isSuccess
                        }
                    }
            awaitClose {
                snapshotListener.remove()
            }
        }
    }

    override suspend fun deletePost(
        postData: PostDataEntity?,
        commentList: List<CommentDataEntity>?
    ): Result<String> {
        return try {
            val uid = auth.currentUser?.uid
            if (postData != null) {
                val postDB = db.collection(COLLECTION_POST).document(postData.postId)
                for (i in 0 until 9) {
                    val storage = storage.getReference("image")
                        .child("${uid}/${postData.postId}/${postData.createdAt}_${i}")
                    postDB.delete()
                    storage.delete()
                }
            }
            Result.success("Success")
        } catch (e: Exception) {
            Result.failure(Exception("Exception: ${e.message}"))
        }

    }

    override suspend fun editPost(postData: PostDataEntity?): Flow<Boolean> {
        return flow {
            try {
                val uid = auth.currentUser?.uid
                val imageList: ArrayList<ImageDataEntity> = arrayListOf()

                if (postData != null) {
                    db.collection(COLLECTION_POST).document(postData.postId).set(postData).await()


                    if (postData.imageList != null) {
                        for (i in 0 until postData.imageList.count()) {
                            val imageToUri = postData.imageList[i].imageUri.toUri()
                            val storageRef = storage.getReference("image")
                                .child("${uid}/${postData.postId}/${postData.createdAt}_${i}")

                            if (imageToUri.pathSegments?.contains("video") == true) {
                                storageRef.putFile(imageToUri).addOnSuccessListener {
                                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                        imageList.add(
                                            ImageDataEntity(
                                                downloadUri.toString(),
                                                postData.imageList[i].imageUri,
                                                "video"
                                            )
                                        )
                                        val imageData =
                                            mapOf("imageList" to imageList.sortedBy { it.downloadUrl })
                                        db.collection(COLLECTION_POST).document(postData.postId)
                                            .update(imageData)
                                    }
                                }
                            } else {
                                storageRef.putFile(imageToUri).addOnSuccessListener {
                                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                        imageList.add(
                                            ImageDataEntity(
                                                downloadUri.toString(),
                                                postData.imageList[i].imageUri,
                                                "image"
                                            )
                                        )
                                        val imageData =
                                            mapOf("imageList" to imageList.sortedBy { it.downloadUrl })
                                        db.collection(COLLECTION_POST).document(postData.postId)
                                            .update(imageData)
                                    }
                                }
                            }
                        }
                    }
                }
                emit(true)
            } catch (e: Exception) {
                emit(false)
            }
        }
    }


    override suspend fun uploadComment(
        commentData: CommentDataEntity?
    ): Flow<Boolean> {
        return flow {
            try {
                if (commentData != null) {
                    db.collection(COLLECTION_COMMENT).document(commentData.commentId)
                        .set(commentData)
                }
                emit(true)
            } catch (e: Exception) {
                emit(false)
            }
        }
    }

    override suspend fun getComment(
        postId: String,
        lastVisibleItem: Flow<Int>
    ): Flow<List<CommentEntity>> {
        return callbackFlow {
            val commentList = mutableListOf<CommentEntity>()
            val commentDocuments = mutableListOf<DocumentSnapshot>()

            lastVisibleItem.collect { lastVisibleItem ->
                Log.d("test_comment_impl1", "${commentList.size}, $lastVisibleItem")
                when (lastVisibleItem) {
                    0 -> {
                        Log.d("test_comment_impl2", "0")
                        db.collection(COLLECTION_COMMENT).whereEqualTo("postId", postId)
                            .orderBy("commentAt", Query.Direction.DESCENDING).limit(2)
                            .addSnapshotListener { commentData, e ->
                                if (e != null) {
                                    trySend(emptyList())
                                } else if (commentData != null) {
                                    val documents = commentData.documents
                                    val commentResponse =
                                        commentData.toObjects(CommentDataResponse::class.java)
                                            .toCommentListEntity()
                                    commentResponse.map { commentEntity ->
                                        db.collection(COLLECTION_USER).document(commentEntity.uid)
                                            .get()
                                            .addOnSuccessListener { userData ->
                                                val userEntity =
                                                    userData.toObject(UserDataResponse::class.java)
                                                        ?.toEntity()
                                                if (userEntity != null) {
                                                    commentList.addAll(
                                                        listOf(
                                                            CommentEntity(
                                                                userEntity,
                                                                commentEntity
                                                            )
                                                        )
                                                    )
                                                }
                                                commentDocuments.addAll(documents)
                                                trySend(commentList)
                                                Log.d("impl", "$commentList")
                                            }
                                    }
                                }
                            }
                    }

                    commentList.size -> {
                        Log.d("test_comment_impl3", "commentList")
                        db.collection(COLLECTION_COMMENT).whereEqualTo("postId", postId)
                            .orderBy("commentAt", Query.Direction.DESCENDING)
                            .startAfter(commentDocuments.last()).limit(2)
                            .addSnapshotListener { commentData, e ->
                                if (e != null) {
                                    trySend(emptyList())
                                } else if (commentData != null) {
                                    val documents = commentData.documents
                                    val commentResponse =
                                        commentData.toObjects(CommentDataResponse::class.java)
                                            .toCommentListEntity()
                                    commentResponse.map { commentEntity ->
                                        db.collection(COLLECTION_USER).document(commentEntity.uid)
                                            .get()
                                            .addOnSuccessListener { userData ->
                                                val userEntity =
                                                    userData.toObject(UserDataResponse::class.java)
                                                        ?.toEntity()
                                                if (userEntity != null) {
                                                    commentList.addAll(
                                                        listOf(
                                                            CommentEntity(
                                                                userEntity,
                                                                commentEntity
                                                            )
                                                        )
                                                    )
                                                }
                                                commentDocuments.addAll(documents)
                                                trySend(commentList)
                                            }

                                    }
                                }
                            }
                    }

                    else -> {
                        commentList.clear()
                        Log.d("test_comment_impl4", "$lastVisibleItem, ${commentList.size}")
                        db.collection(COLLECTION_COMMENT).whereEqualTo("postId", postId)
                            .orderBy("commentAt", Query.Direction.DESCENDING)
                            .limit(lastVisibleItem.toLong())
                            .addSnapshotListener { commentData, e ->
                                if (e != null) {
                                    trySend(emptyList())
                                } else if (commentData != null) {
                                    val documents = commentData.documents
                                    val commentResponse =
                                        commentData.toObjects(CommentDataResponse::class.java)
                                            .toCommentListEntity()
                                    commentResponse.map { commentEntity ->
                                        db.collection(COLLECTION_USER)
                                            .document(commentEntity.uid)
                                            .get()
                                            .addOnSuccessListener { userData ->
                                                val userEntity =
                                                    userData.toObject(UserDataResponse::class.java)
                                                        ?.toEntity()
                                                if (userEntity != null) {
                                                    commentList.addAll(
                                                        listOf(
                                                            CommentEntity(
                                                                userEntity,
                                                                commentEntity
                                                            )
                                                        )
                                                    )
                                                }
                                                commentDocuments.addAll(documents)
                                                trySend(commentList)
                                            }
                                    }
                                }
                            }
                    }
                }
            }
            awaitClose()
        }
    }

    override suspend fun deleteComment(
        commentId: String,
    ): Result<String> {
        return try {
            val commentDB = db.collection(COLLECTION_COMMENT).document(commentId)

            db.collection(COLLECTION_RE_COMMENT).whereEqualTo("commentId", commentId).get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        for (i in 0 until it.size()) {
                            val reCommentData =
                                it.documents[i].toObject(ReCommentDataResponse::class.java)
                            if (reCommentData?.reCommentId != null) {
                                db.collection(COLLECTION_RE_COMMENT)
                                    .document(reCommentData.reCommentId)
                                    .delete()
                            }
                        }
                        commentDB.delete()
                    } else {
                        commentDB.delete()
                    }
                }

            Result.success("success")
        } catch (e: Exception) {
            Result.failure(Exception("exception: $e"))
        }
    }

    override suspend fun uploadReComment(
        reCommentData: ReCommentDataEntity?
    ): Flow<Boolean> {
        return flow {
            try {
                if (reCommentData != null) {
                    db.collection(COLLECTION_RE_COMMENT).document(reCommentData.reCommentId)
                        .set(reCommentData).await()
                    db.collection(COLLECTION_RE_COMMENT)
                        .whereEqualTo("commentId", reCommentData.commentId)
                        .get().addOnCompleteListener { task ->
                            val taskSize = task.result.size()
                            val reCommentSize = mapOf("reCommentSize" to taskSize)
                            db.collection(COLLECTION_COMMENT).document(reCommentData.commentId)
                                .update(reCommentSize)
                        }
                }
                emit(true)
            } catch (e: Exception) {
                emit(false)
            }
        }
    }

    override suspend fun getReComment(
        commentId: String,
        lastVisibleItem: Flow<Int>
    ): Flow<List<ReCommentEntity>> {
        return callbackFlow {
            val reCommentList = mutableListOf<ReCommentEntity>()
            val reCommentDocuments = mutableListOf<DocumentSnapshot>()

            lastVisibleItem.collect { lastVisibleItem ->
                when (lastVisibleItem) {

                    0 -> db.collection(COLLECTION_RE_COMMENT).whereEqualTo("commentId", commentId)
                        .orderBy("commentAt", Query.Direction.DESCENDING).limit(2)
                        .addSnapshotListener { reCommentData, e ->
                            if (e != null) {
                                trySend(emptyList())
                            } else if (reCommentData != null) {
                                val documents = reCommentData.documents
                                val reCommentResponse =
                                    reCommentData.toObjects(ReCommentDataResponse::class.java)
                                        .toReCommentListEntity()
                                reCommentResponse.map { reCommentEntity ->
                                    db.collection(COLLECTION_USER).document(reCommentEntity.uid)
                                        .get()
                                        .addOnSuccessListener { userData ->
                                            val userEntity =
                                                userData.toObject(UserDataResponse::class.java)
                                                    ?.toEntity()
                                            if (userEntity != null) {
                                                reCommentList.addAll(
                                                    listOf(
                                                        ReCommentEntity(
                                                            userEntity,
                                                            reCommentEntity
                                                        )
                                                    )
                                                )
                                            }
                                            reCommentDocuments.addAll(documents)
                                            trySend(reCommentList)
                                        }
                                }
                            }
                        }

                    reCommentList.size -> {
                        db.collection(COLLECTION_RE_COMMENT).whereEqualTo("commentId", commentId)
                            .orderBy("commentAt", Query.Direction.DESCENDING)
                            .startAfter(reCommentDocuments.last()).limit(2)
                            .addSnapshotListener { reCommentData, e ->
                                if (e != null) {
                                    trySend(emptyList())
                                } else if (reCommentData != null) {
                                    val documents = reCommentData.documents
                                    val reCommentResponse =
                                        reCommentData.toObjects(ReCommentDataResponse::class.java)
                                            .toReCommentListEntity()
                                    reCommentResponse.map { reCommentEntity ->
                                        db.collection(COLLECTION_USER).document(reCommentEntity.uid)
                                            .get()
                                            .addOnSuccessListener { userData ->
                                                val userEntity =
                                                    userData.toObject(UserDataResponse::class.java)
                                                        ?.toEntity()
                                                if (userEntity != null) {
                                                    reCommentList.addAll(
                                                        listOf(
                                                            ReCommentEntity(
                                                                userEntity,
                                                                reCommentEntity
                                                            )
                                                        )
                                                    )
                                                }
                                                reCommentDocuments.addAll(documents)
                                                trySend(reCommentList)
                                            }
                                    }
                                }
                            }
                    }

                    else -> {
                        trySend(emptyList())
                    }
                }
            }
            awaitClose()
        }
    }

    override suspend fun deleteReComment(
        commentId: String,
        reCommentId: String
    ): Result<String> {
        return try {
            val reCommentDB = db.collection(COLLECTION_RE_COMMENT).document(reCommentId)
            reCommentDB.delete().await()
            db.collection(COLLECTION_RE_COMMENT).whereEqualTo("commentId", commentId).get()
                .addOnCompleteListener { task ->
                    val taskSize = task.result.size()
                    val reCommentSize = mapOf("reCommentSize" to taskSize)
                    db.collection(COLLECTION_COMMENT).document(commentId).update(reCommentSize)
                }
            Result.success("success")
        } catch (e: Exception) {
            Result.failure(Exception("failure: ${e.message}"))
        }
    }

    override suspend fun getPagingPost(lastVisibleItem: Flow<Int>): Flow<List<PostEntity>?> {
        return callbackFlow {
            val postList = mutableListOf<PostEntity>()
            val postDocuments = mutableListOf<DocumentSnapshot>()
            lastVisibleItem.collect { lastVisibleItem ->
                when (lastVisibleItem) {
                    0 -> {
                        db.collection(COLLECTION_POST)
                            .orderBy("createdAt", Query.Direction.DESCENDING)
                            .limit(3).get().addOnSuccessListener { postData ->
                                val documents = postData.documents
                                val postResponse = postData.toObjects(PostDataResponse::class.java)
                                    .toPostListEntity()
                                postResponse.map { postEntity ->
                                    db.collection(COLLECTION_USER).document(postEntity.uid).get()
                                        .addOnSuccessListener { userData ->
                                            val userEntity =
                                                userData.toObject(UserDataResponse::class.java)
                                                    ?.toEntity()
                                            if (userEntity != null) {
                                                postList.addAll(
                                                    listOf(
                                                        PostEntity(
                                                            userEntity, postEntity
                                                        )
                                                    )
                                                )
                                            }
                                            postDocuments.addAll(documents)
                                            trySend(postList)
                                        }
                                }
                            }
                    }

                    postList.size -> {
                        db.collection(COLLECTION_POST)
                            .orderBy("createdAt", Query.Direction.DESCENDING)
                            .startAfter(postDocuments.last()).limit(3)
                            .get().addOnSuccessListener { postData ->
                                val documents = postData.documents
                                val postResponse =
                                    postData.toObjects(PostDataResponse::class.java)
                                        .toPostListEntity()
                                postResponse.map { postEntity ->
                                    db.collection(COLLECTION_USER).document(postEntity.uid).get()
                                        .addOnSuccessListener { userData ->
                                            val userEntity =
                                                userData.toObject(UserDataResponse::class.java)
                                                    ?.toEntity()
                                            if (userEntity != null) {
                                                postList.addAll(
                                                    listOf(
                                                        PostEntity(
                                                            userEntity,
                                                            postEntity
                                                        )
                                                    )
                                                )
                                            }
                                            postDocuments.addAll(documents)
                                            trySend(postList)
                                        }
                                }
                            }
                    }

                    else -> {
                        db.collection(COLLECTION_POST)
                            .orderBy("createdAt", Query.Direction.DESCENDING)
                            .limit(lastVisibleItem.toLong()).get()
                            .addOnSuccessListener { postData ->
                                val documents = postData.documents
                                val postResponse = postData.toObjects(PostDataResponse::class.java)
                                    .toPostListEntity()
                                postResponse.map { postEntity ->
                                    db.collection(COLLECTION_USER).document(postEntity.uid).get()
                                        .addOnSuccessListener { userData ->
                                            val userEntity =
                                                userData.toObject(UserDataResponse::class.java)
                                                    ?.toEntity()
                                            if (userEntity != null) {
                                                postList.addAll(
                                                    listOf(
                                                        PostEntity(
                                                            userEntity,
                                                            postEntity
                                                        )
                                                    )
                                                )
                                            }
                                            postDocuments.addAll(documents)
                                            trySend(postList)
                                        }
                                }
                            }
                    }
                }
            }
            awaitClose()
        }
    }

    override suspend fun checkChatRoom(
        recipientUid: String
    ): Flow<Boolean> {
        return callbackFlow {
            val senderUid = auth.currentUser?.uid
            if (senderUid != null) {
                db.collection(COLLECTION_CHAT)
                    .whereArrayContainsAny("participant", listOf(senderUid, recipientUid))
                    .get().addOnSuccessListener { snapshot ->
//                        if (e != null) {
//                            Log.d("check_chat2", "${e.message}")
//                            trySend(false)
//                        }
                        if (snapshot != null) {
                            val document = snapshot.documents
                            if (document.size != 0) {
                                Log.d("check_chat1", "$document")
                                trySend(true)
                            } else {
                                Log.d("check_chat2", "null data")
                                trySend(false)
                            }
                        }
                    }
            }
            awaitClose()
        }
    }

    override suspend fun getChatRoomData(recipientUid: String): Flow<ChatRoomDataEntity?> {
        return callbackFlow {
            val senderUid = auth.currentUser?.uid
            if (senderUid != null) {
                db.collection(COLLECTION_CHAT)
                    .whereArrayContains("participant", senderUid)
                    .whereArrayContains("participant", recipientUid)
                    .addSnapshotListener { chatRoomData, e ->
                        if (e != null) {
                            trySend(null)
                        }
                        if (chatRoomData != null) {
                            val chatRoomEntity =
                                chatRoomData.toObjects(ChatRoomDataResponse::class.java).first()
                                    .toEntity()
                            trySend(chatRoomEntity)
                        } else {
                            trySend(null)
                        }
                    }
            }
            awaitClose()
        }
    }

    override suspend fun sendFirstMessage(
        chatRoomId: String,
        token: String,
        sendUser: String,
        accessToken: String,
        senderUid: String,
        recipientUid: String,
        messageData: UploadMessageDataEntity
    ): Flow<Boolean> {
        return flow {
            try {
                if (messageData.message != null) {
                    val participant = arrayListOf<String>()
                    participant.add(senderUid)
                    participant.add(recipientUid)
                    val chatRoomSession = arrayListOf(
                        mapOf(senderUid to true),
                        mapOf(recipientUid to false)
                    )
                    val lastMessageData = hashMapOf(
                        "lastMessage" to messageData.message,
                        "lastSendAt" to messageData.sendAt,
                        "lastMessageSender" to senderUid
                    )
                    val chatRoomData = hashMapOf(
                        "chatRoomId" to chatRoomId,
                        "participant" to participant,
                        "chatRoomSession" to chatRoomSession,
                        "lastMessageData" to lastMessageData
                    )
                    val chatRoomDB = db.collection(COLLECTION_CHAT).document(chatRoomId)

                    chatRoomDB.set(chatRoomData).addOnSuccessListener {
                        chatRoomDB.collection(COLLECTION_CHAT_MESSAGE)
                            .document(messageData.messageId).set(messageData)
                            .addOnSuccessListener {
                                chatRoomDB.collection(COLLECTION_CHAT_MESSAGE)
                                    .whereArrayContains("read", mapOf(recipientUid to false))
                                    .addSnapshotListener { unReadMessage, e ->
                                        if (e != null) {
                                            Log.d(TAG, "$e")
                                        }
                                        chatRoomDB.update("unReadMessage", unReadMessage?.size())
                                    }
                            }
                    }.addOnSuccessListener {
                        FirebaseMessagingService().sendNotifications(
                            accessToken = accessToken,
                            token = token,
                            title = sendUser,
                            content = messageData.message,
                            chatRoomId = chatRoomId,
                            uid = messageData.uid
                        )
                    }
                }
                emit(true)
            } catch (e: Exception) {
                emit(false)
            }
        }
    }

    override suspend fun sendMessage(
        chatRoomId: String,
        token: String,
        sendUser: String,
        accessToken: String,
        recipientUid: String,
        messageData: UploadMessageDataEntity
    ): Flow<Boolean> {
        return flow {
            try {
                val imageList: MutableList<ImageDataEntity> = arrayListOf()
                val lastMessageSender = auth.currentUser?.uid
                val chatRoomDB = db.collection(COLLECTION_CHAT).document(chatRoomId)
                val messageDB =
                    chatRoomDB.collection(COLLECTION_CHAT_MESSAGE).document(messageData.messageId)
                if (messageData.message != null) {
                    messageDB.set(messageData).addOnSuccessListener {
                        chatRoomDB.collection(COLLECTION_CHAT_MESSAGE)
                            .whereArrayContains("read", mapOf(recipientUid to false))
                            .addSnapshotListener { unReadMessage, e ->
                                if (e != null) {
                                    Log.d(TAG, "$e")
                                }
                                val lastMessageData = hashMapOf(
                                    "lastMessage" to messageData.message,
                                    "lastSendAt" to messageData.sendAt,
                                    "lastMessageSender" to lastMessageSender
                                )
                                chatRoomDB.update("lastMessageData", lastMessageData)
                                chatRoomDB.update("unReadMessage", unReadMessage?.size())
                            }
                    }.addOnSuccessListener {
                        if (messageData.read.contains(mapOf(recipientUid to false))) {
                            Log.d(TAG, "FCM 보내기 완료")
                            FirebaseMessagingService().sendNotifications(
                                accessToken = accessToken,
                                token = token,
                                title = sendUser,
                                content = messageData.message,
                                chatRoomId = chatRoomId,
                                uid = messageData.uid
                            )
                        }
                    }
                } else if (messageData.imageList != null) {
                    // FIXME: 이미지 파일 완전히 올린 후에 messageDB에 set
                    val message = hashMapOf(
                        "uid" to messageData.uid,
                        "chatRoomId" to messageData.chatRoomId,
                        "messageId" to messageData.messageId,
                        "message" to null,
                        "sendAt" to messageData.sendAt,
                        "read" to messageData.read,
                        "type" to messageData.type
                    )
                    messageDB.set(message).addOnSuccessListener {
                        for (i in 0 until messageData.imageList.count()) {
                            val imageToUri = messageData.imageList[i]
                            val storageRef = storage.getReference("chat")
                                .child("${chatRoomId}/${messageData.sendAt}/${i}")
                            if (imageToUri.pathSegments?.contains("video") == true) {
                                storageRef.putFile(imageToUri).addOnSuccessListener {
                                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                        imageList.add(
                                            ImageDataEntity(
                                                downloadUrl.toString(),
                                                imageToUri.toString(),
                                                "video"
                                            )
                                        )
                                        val imageData =
                                            mapOf("imageList" to imageList.sortedBy { it.downloadUrl })
                                        messageDB.update(imageData)
                                    }
                                }
                            } else {
                                storageRef.putFile(imageToUri).addOnSuccessListener {
                                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                        imageList.add(
                                            ImageDataEntity(
                                                downloadUrl.toString(),
                                                imageToUri.toString(),
                                                "image"
                                            )
                                        )
                                        val imageData =
                                            mapOf("imageList" to imageList.sortedBy { it.downloadUrl })
                                        messageDB.update(imageData)
                                    }
                                }
                            }
                        }
                    }.addOnSuccessListener {
                        chatRoomDB.update("lastMessage", "이미지 파일")
                        chatRoomDB.update("lastSendAt", messageData.sendAt)
                    }.addOnSuccessListener {
                        if (messageData.read.contains(mapOf(recipientUid to false))) {
                            FirebaseMessagingService().sendNotifications(
                                accessToken = accessToken,
                                token = token,
                                title = sendUser,
                                content = "이미지 파일이 도착했습니다.",
                                chatRoomId = chatRoomId,
                                uid = messageData.uid
                            )
                        }
                    }
                }
                emit(true)
            } catch (e: Exception) {
                emit(false)
            }
        }
    }

    override suspend fun getUserSession(recipientUid: String, chatRoomId: String): Flow<Boolean> {
        return callbackFlow {
            val chatRef = db.collection(COLLECTION_CHAT).document(chatRoomId)
            Log.d("ChatRoomFragment1", "$recipientUid, $chatRoomId")

            chatRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(false)
                }
                if (snapshot != null) {
                    Log.d("ChatRoomFragment1", "$snapshot")
                    val session =
                        snapshot.toObject(ChatRoomDataResponse::class.java)?.chatRoomSession
                    if (session != null) {
                        if (session.contains(mapOf(recipientUid to true))) {
                            trySend(true)
                        } else if (session.contains(mapOf(recipientUid to false))) {
                            trySend(false)
                        }
                    } else {
                        trySend(false)
                    }
                }
            }
            awaitClose()
        }
    }


    override suspend fun checkReadMessage(chatRoomId: String, userSession: Boolean, recipientUid: String): Flow<Boolean> {
        return callbackFlow {
            try {
                val currentUserUid = auth.currentUser?.uid
                val chatRef = db.collection(COLLECTION_CHAT).document(chatRoomId)
                val messageRef = chatRef.collection(COLLECTION_CHAT_MESSAGE)
                Log.d(TAG, "$userSession")

                if (userSession) {
                    db.runTransaction {
                        it.update(
                            chatRef, "chatRoomSession", FieldValue.arrayRemove(
                                mapOf(currentUserUid to false)
                            )
                        )
                        it.update(
                            chatRef, "chatRoomSession", FieldValue.arrayUnion(
                                mapOf(currentUserUid to true)
                            )
                        )
                    }.addOnSuccessListener {
                        chatRef.collection(COLLECTION_CHAT_MESSAGE)
                            .whereArrayContains("read", mapOf(recipientUid to false))
                            .addSnapshotListener { unReadMessage, e ->
                                if (e != null) {
                                    Log.d(TAG, "$e")
                                }
                                chatRef.update("unReadMessage", unReadMessage?.size())
                            }
                        trySend(true)
                    }.await()

                    messageRef.whereArrayContains("read", mapOf(currentUserUid to false)).get()
                        .addOnSuccessListener { unReadMessage ->
                            if (unReadMessage?.documents?.size != 0) {
                                unReadMessage?.documents?.forEach { unReadMessageDocument ->
                                    db.runTransaction {
                                        it.update(
                                            unReadMessageDocument.reference,
                                            "read",
                                            FieldValue.arrayRemove(
                                                mapOf(currentUserUid to false)
                                            )
                                        )
                                        it.update(
                                            unReadMessageDocument.reference,
                                            "read",
                                            FieldValue.arrayUnion(
                                                mapOf(currentUserUid to true)
                                            )
                                        )
                                    }
                                }
                            } else {
                                Log.d(TAG, "안읽은 메세지 없음")
                            }
                        }
                } else {
                    db.runTransaction {
                        it.update(
                            chatRef, "chatRoomSession", FieldValue.arrayRemove(
                                mapOf(currentUserUid to true)
                            )
                        )
                        it.update(
                            chatRef, "chatRoomSession", FieldValue.arrayUnion(
                                mapOf(currentUserUid to false)
                            )
                        )
                    }.addOnSuccessListener {
                        trySend(false)
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "$e")
            }
            awaitClose()
        }
    }

    override suspend fun checkChatRoomList(): Flow<Boolean> {
        return callbackFlow {
            val currentUser = auth.currentUser?.uid
            if (currentUser != null) {
                db.collection(COLLECTION_CHAT).whereArrayContains("participant", currentUser)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            trySend(false)
                        }
                        if (snapshot != null) {
                            val document = snapshot.documents.size
                            if (document != 0) {
                                trySend(true)
                            } else {
                                trySend(false)
                            }
                        }
                    }
            }
            awaitClose()
        }
    }


    override suspend fun getChatRoomList(): Flow<List<ChatRoomEntity>> {
        return callbackFlow {
            val currentUser = auth.currentUser?.uid
            val chatRoomList = mutableListOf<ChatRoomEntity>()
            if (currentUser != null) {
                db.collection(COLLECTION_CHAT).whereArrayContains("participant", currentUser).get()
                    .addOnSuccessListener { snapshot ->
//                    if (e != null) {
//                        trySend(emptyList())
//                    }
                        if (snapshot != null) {
                            val chatRoomData = snapshot.toObjects(ChatRoomDataResponse::class.java)
                                .toChatRoomListEntity()
                            chatRoomData.map { chatRoomEntity ->
                                val userData = chatRoomEntity.participant.minus(currentUser).first()
                                db.collection(COLLECTION_USER).document(userData).get()
                                    .addOnSuccessListener { userResponse ->
                                        val userEntity =
                                            userResponse.toObject(UserDataResponse::class.java)
                                                ?.toEntity()
                                        if (userEntity != null) {
                                            chatRoomList.addAll(
                                                listOf(
                                                    ChatRoomEntity(
                                                        userEntity,
                                                        chatRoomEntity
                                                    )
                                                )
                                            )
                                        }
                                        trySend(chatRoomList)
                                    }
                            }
                        }
                    }
            }
            awaitClose()
        }
    }

    override suspend fun deleteChatRoom(chatRoomId: String): Flow<Boolean> {
        return callbackFlow {
            val chatRef = db.collection(COLLECTION_CHAT).document(chatRoomId)
            val messageRef = chatRef.collection(COLLECTION_CHAT_MESSAGE)
            messageRef.whereEqualTo("chatRoomId", chatRoomId).get().addOnSuccessListener { documents ->
                documents.toObjects(MessageDataResponse::class.java).map { messageData ->
                    for (i in 0 until documents.size()) {
                        messageRef.document(messageData.messageId).delete()
                    }
                }
            }.addOnSuccessListener {
                chatRef.delete()
            }.addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    trySend(true)
                } else {
                    trySend(false)
                }
            }
            awaitClose()
        }
    }

    override suspend fun checkMessageData(chatRoomId: String): Flow<Boolean> {
        return callbackFlow {
            val query = db.collection(COLLECTION_CHAT).document(chatRoomId)
                .collection(COLLECTION_CHAT_MESSAGE).whereEqualTo("chatRoomId", chatRoomId)
            query.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(false)
                }
                if (snapshot != null) {
                    val document = snapshot.documents.size
                    if (document != 0) {
                        trySend(true)
                    } else {
                        trySend(false)
                    }
                }
            }
            awaitClose()
        }
    }


    override suspend fun getChatMessageData(
        chatRoomId: String,
        lastVisibleItem: Flow<Int>
    ): Flow<List<MessageEntity>> {
        return callbackFlow {
            val messageList = mutableListOf<MessageEntity>()
            val messageDocuments = mutableListOf<DocumentSnapshot>()
            val query = db.collection(COLLECTION_CHAT).document(chatRoomId)
                .collection(COLLECTION_CHAT_MESSAGE)
                .whereEqualTo("chatRoomId", chatRoomId)
                .orderBy("sendAt", Query.Direction.ASCENDING)

            lastVisibleItem.collect { lastVisibleItem ->
                when (lastVisibleItem) {
                    0 -> query
                        .get().addOnSuccessListener { snapshot ->
                            if (snapshot != null) {
                                val documents = snapshot.documents
                                val messageListEntity =
                                    snapshot.toObjects(MessageDataResponse::class.java)
                                        .toMessageListEntity()
                                messageListEntity.map { messageEntity ->
                                    db.collection(COLLECTION_USER).document(messageEntity.uid).get()
                                        .addOnSuccessListener { userData ->
                                            val userEntity =
                                                userData.toObject(UserDataResponse::class.java)
                                                    ?.toEntity()
                                            if (userEntity != null) {
                                                messageList.addAll(
                                                    listOf(
                                                        MessageEntity(
                                                            userEntity,
                                                            messageEntity
                                                        )
                                                    )
                                                )
                                            }
                                            messageDocuments.addAll(documents)
                                            trySend(messageList)
                                        }
                                }
                            }
                        }

                    messageList.size -> query.startAfter(messageDocuments.last()).limit(10)
                        .addSnapshotListener { snapshot, e ->
                            if (e != null) {
                                trySend(emptyList())
                            }
                            if (snapshot != null) {
                                val documents = snapshot.documents
                                val messageListEntity =
                                    snapshot.toObjects(MessageDataResponse::class.java)
                                        .toMessageListEntity()
                                messageListEntity.map { messageEntity ->
                                    db.collection(COLLECTION_USER).document(messageEntity.uid).get()
                                        .addOnSuccessListener { userData ->
                                            val userEntity =
                                                userData.toObject(UserDataResponse::class.java)
                                                    ?.toEntity()
                                            if (userEntity != null) {
                                                messageList.addAll(
                                                    listOf(
                                                        MessageEntity(
                                                            userEntity,
                                                            messageEntity
                                                        )
                                                    )
                                                )
                                            }
                                            messageDocuments.addAll(documents)
                                            trySend(messageList)
                                        }
                                }
                            }
                        }

                    else -> {
                        Log.d("message_else", "message_else")
                    }
                }
            }
            awaitClose()
        }
    }

    override suspend fun searchUserData(query: String): Flow<List<UserDataEntity>> {
        return callbackFlow {
            val userDataList: ArrayList<UserDataEntity> = arrayListOf()
            db.collection(COLLECTION_USER)
                .addSnapshotListener { querySnapShot, e ->
                    if (e != null) {
                        trySend(emptyList())
                    }
                    if (querySnapShot != null) {
                        val userData =
                            querySnapShot.toObjects(UserDataResponse::class.java).toUserListEntity()
                        userData.map {
                            if (it.name.contains(query)) {
                                userDataList.add(it)
                            }
                        }
//                        for (snapshot in userData) {
//                            if (snapshot.name.contains(keyword)) {
//                                userDataList.add(snapshot)
//                                Log.d("searchUserData", "$snapshot")
//                            }
//                        }
                        trySend(userDataList)
                    }
                }
            awaitClose()
        }
    }

    override suspend fun searchPostData(query: String): Flow<List<PostDataEntity>> {
        return callbackFlow {
            val postDataList: ArrayList<PostDataEntity> = arrayListOf()
            db.collection(COLLECTION_POST)
                .addSnapshotListener { querySnapShot, e ->
                    if (e != null) {
                        trySend(emptyList())
                    }
                    if (querySnapShot != null) {
                        val postData =
                            querySnapShot.toObjects(PostDataResponse::class.java).toPostListEntity()
                        postData.map {
                            val postText = it.postText ?: throw NullPointerException("postTextNull")
                            if (postText.contains(query)) {
                                postDataList.add(it)
                            }
                        }
                        trySend(postDataList.sortedBy { it.createdAt })
                    }
                }
            awaitClose()
        }
    }

    override suspend fun getPostByPostId(postId: String): Flow<PostEntity?> {
        return callbackFlow {
            val postRef = db.collection(COLLECTION_POST).document(postId)
            postRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(null)
                }
                if (snapshot != null) {
                    val postDataEntity = snapshot.toObject(PostDataResponse::class.java)?.toEntity()
                    val uid = postDataEntity?.uid
                    if (uid != null) {
                        db.collection(COLLECTION_USER).document(uid).get().addOnSuccessListener { userData ->
                            val userDataEntity = userData.toObject(UserDataResponse::class.java)?.toEntity()
                            if (userDataEntity != null) {
                                trySend(PostEntity(userDataEntity, postDataEntity))
                            } else {
                                trySend(null)
                            }
                        }
                    }
                }
            }
            awaitClose()
        }
    }

    override suspend fun updateLike(postId: String, likeValue: Boolean): Flow<Boolean> {
        return callbackFlow {
            val currentUser = auth.currentUser?.uid
            val postRef = db.collection(COLLECTION_POST).document(postId)
            db.runTransaction { transaction ->
                if (likeValue) {
                    transaction.update(postRef, "likePost", FieldValue.arrayUnion(currentUser))
                } else {
                    transaction.update(postRef, "likePost", FieldValue.arrayRemove(currentUser))
                }
            }
            awaitClose()
        }
    }

}