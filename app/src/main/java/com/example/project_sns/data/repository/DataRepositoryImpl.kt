package com.example.project_sns.data.repository

import android.util.Log
import androidx.core.net.toUri
import com.example.project_sns.data.mapper.toCommentListEntity
import com.example.project_sns.data.mapper.toEntity
import com.example.project_sns.data.mapper.toPostListEntity
import com.example.project_sns.data.mapper.toReCommentListEntity
import com.example.project_sns.data.response.ChatRoomDataResponse
import com.example.project_sns.data.response.CommentDataResponse
import com.example.project_sns.data.response.PostDataResponse
import com.example.project_sns.data.response.ReCommentDataResponse
import com.example.project_sns.data.response.UserDataResponse
import com.example.project_sns.data.response.toEntity
import com.example.project_sns.domain.entity.ChatRoomDataEntity
import com.example.project_sns.domain.entity.CommentDataEntity
import com.example.project_sns.domain.entity.CommentEntity
import com.example.project_sns.domain.entity.ImageDataEntity
import com.example.project_sns.domain.entity.MessageDataEntity
import com.example.project_sns.domain.entity.PostDataEntity
import com.example.project_sns.domain.entity.PostEntity
import com.example.project_sns.domain.entity.ReCommentDataEntity
import com.example.project_sns.domain.entity.ReCommentEntity
import com.example.project_sns.domain.repository.DataRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

private const val COLLECTION_USER = "user"
private const val COLLECTION_POST = "post"
private const val COLLECTION_COMMENT = "comment"
private const val COLLECTION_RE_COMMENT = "reComment"
private const val COLLECTION_CHAT = "chat"
private const val COLLECTION_CHAT_MESSAGE = "message"

private const val TAG = "data_repo_impl"

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
                        "editedAt" to postData.editedAt
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
                                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                        imageList.add(
                                            ImageDataEntity(
                                                downloadUri.toString(),
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
                                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                        imageList.add(
                                            ImageDataEntity(
                                                downloadUri.toString(),
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
                    Log.d(TAG, "$imageList")
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
                when (lastVisibleItem) {

                    0 -> {
                        db.collection(COLLECTION_COMMENT).whereEqualTo("postId", postId)
                            .orderBy("commentAt", Query.Direction.DESCENDING).limit(3)
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
                                                Log.d(TAG, "$documents")
                                            }
                                    }
                                }
                            }
                    }

                    commentList.size -> {
                        db.collection(COLLECTION_COMMENT).whereEqualTo("postId", postId)
                            .orderBy("commentAt", Query.Direction.DESCENDING)
                            .startAfter(commentDocuments.last()).limit(3)
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
                                                    Log.d(TAG, "${commentList}")
                                                }
                                                commentDocuments.addAll(documents)
                                                trySend(commentList)
                                                Log.d(TAG, "${commentList}, $documents")
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
                            Log.d(TAG, "$taskSize")
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
                        .orderBy("commentAt", Query.Direction.DESCENDING).limit(3)
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
                                            Log.d(TAG, "$reCommentList")
                                        }
                                }
                            }
                        }

                    reCommentList.size -> {
                        db.collection(COLLECTION_RE_COMMENT).whereEqualTo("commentId", commentId)
                            .orderBy("commentAt", Query.Direction.DESCENDING)
                            .startAfter(reCommentDocuments.last()).limit(3)
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
                                                Log.d(TAG, "$reCommentList")
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
                    Log.d(TAG, "$taskSize")
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
                                            Log.d(TAG, "$postList")
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
                                            Log.d(TAG, "$postList")
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
        senderUid: String,
        recipientUid: String
    ): Flow<ChatRoomDataEntity?> {
        return callbackFlow {
            val query = db.collection(COLLECTION_CHAT).whereEqualTo("senderUid", senderUid)
                .whereEqualTo("recipientUid", recipientUid)
            query.get().addOnSuccessListener { chatData ->
                if (chatData != null) {
                    val snapshotList = chatData.toObjects(ChatRoomDataResponse::class.java)
                    val chatList = mutableListOf<ChatRoomDataResponse>()
                    chatList.addAll(snapshotList)
                    if (chatList.isNotEmpty()) {
                        Log.d("Chat_Tag_Success", "$snapshotList")
                        val chatRoomDataEntity = chatList[0].toEntity()
                        trySend(chatRoomDataEntity)
                    } else {
                        Log.d("Chat_Tag_Fail", "$snapshotList")
                        trySend(null)
                    }
                }
            }
            awaitClose()
        }
    }

    override suspend fun sendFirstMessage(
        chatRoomId: String,
        senderUid: String,
        recipientUid: String,
        messageData: MessageDataEntity
    ): Flow<Boolean> {
        return flow {
            try {
                val chatRoomData = hashMapOf(
                    "chatRoomId" to chatRoomId,
                    "senderUid" to senderUid,
                    "recipientUid" to recipientUid
                )
                val chatRoomDB = db.collection(COLLECTION_CHAT).document(chatRoomId)

                chatRoomDB.set(chatRoomData).addOnSuccessListener {
                    chatRoomDB.collection(COLLECTION_CHAT_MESSAGE).document(messageData.messageId).set(messageData)
                }
                emit(true)
            } catch (e: Exception) {
                emit(false)
            }
        }
    }

    override suspend fun sendMessage(
        chatRoomId: String,
        messageData: MessageDataEntity
    ): Flow<Boolean> {
        return flow {
            try {
                Log.d("tag_impl_send", chatRoomId)
                val chatRoomDB = db.collection(COLLECTION_CHAT).document(chatRoomId)
                val messageDB = chatRoomDB.collection(COLLECTION_CHAT_MESSAGE).document(messageData.messageId)
                messageDB.set(messageData).await()
                emit(true)
            } catch (e: Exception) {
                emit(false)
            }
        }
    }
}