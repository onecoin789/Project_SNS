package com.example.project_sns.data.repository

import android.util.Log
import androidx.core.net.toUri
import com.example.project_sns.data.mapper.toCommentListEntity
import com.example.project_sns.data.mapper.toEntity
import com.example.project_sns.data.mapper.toPostListEntity
import com.example.project_sns.data.mapper.toReCommentListEntity
import com.example.project_sns.data.response.CommentDataResponse
import com.example.project_sns.data.response.PostDataResponse
import com.example.project_sns.data.response.ReCommentDataResponse
import com.example.project_sns.data.response.UserDataResponse
import com.example.project_sns.domain.model.CommentDataEntity
import com.example.project_sns.domain.model.ImageDataEntity
import com.example.project_sns.domain.model.PostDataEntity
import com.example.project_sns.domain.model.PostEntity
import com.example.project_sns.domain.model.ReCommentDataEntity
import com.example.project_sns.domain.model.UserDataEntity
import com.example.project_sns.domain.repository.DataRepository
import com.example.project_sns.ui.mapper.toPostDataListModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
                        "profileImage" to postData.profileImage,
                        "name" to postData.name,
                        "email" to postData.email,
                        "postText" to postData.postText,
                        "mapData" to postData.mapData,
                        "createdAt" to postData.createdAt,
                        "editedAt" to postData.editedAt
                    )
                    db.collection("post").document(postData.postId).set(data).await()
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
                                        db.collection("post").document(postData.postId)
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
                                        db.collection("post").document(postData.postId)
                                            .update(imageData)
                                    }
                                }
                            }
                        }
                    }
                    Log.d("test_data", "$imageList")
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
                val postData = db.collection("post").whereEqualTo("uid", uid)
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
                db.collection("post").orderBy("createdAt", Query.Direction.DESCENDING)
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
                val postDB = db.collection("post").document(postData.postId)
                if (commentList != null) {
                    for (element in commentList) {
                        val commentDB = postDB.collection("comment").document(element.commentId)
                        commentDB.delete().await()
                    }
                }

                val reCommentDB = postDB.collection("comment").document()

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
                    db.collection("post").document(postData.postId).set(postData).await()


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
                                        db.collection("post").document(postData.postId)
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
                                        db.collection("post").document(postData.postId)
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
        postId: String,
        commentData: CommentDataEntity?
    ): Flow<Boolean> {

        return flow {
            try {
                if (commentData != null) {
                    db.collection("post").document(postId)
                        .collection("comment")
                        .document(commentData.commentId)
                        .set(commentData)
                }
                emit(true)
            } catch (e: Exception) {
                emit(false)
            }
        }
    }

    override suspend fun getComment(postId: String): Flow<List<CommentDataEntity>> {
        return callbackFlow {
            val commentData = db.collection("post").document(postId).collection("comment")
            val snapShotListener = commentData.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList()).isSuccess
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val commentResponse = snapshot.toObjects(CommentDataResponse::class.java)
                    trySend(commentResponse.toCommentListEntity()).isSuccess
                } else {
                    trySend(emptyList()).isSuccess
                }
            }
            awaitClose {
                snapShotListener.remove()
            }
        }
    }

    override suspend fun deleteComment(
        postId: String,
        commentId: String,
        reCommentList: List<ReCommentDataEntity>?
    ): Result<String> {
        return try {
            val postDB = db.collection("post").document(postId)
            val commentDB = postDB.collection("comment").document(commentId)
            Log.d("test_impl", "$postId, $commentId")

            if (reCommentList.isNullOrEmpty()) {
                commentDB.delete()
            } else {
                for (element in reCommentList) {
                    val reCommentDB = commentDB.collection("reComment").document(element.commentId)
                    reCommentDB.delete().await()
                    commentDB.delete()
                }
            }
            Result.success("success")
        } catch (e: Exception) {
            Result.failure(Exception("exception: $e"))
        }
    }

    override suspend fun uploadReComment(
        postId: String,
        commentId: String,
        reCommentData: ReCommentDataEntity?
    ): Flow<Boolean> {
        return flow {
            try {
                if (reCommentData != null) {
                    val reCommentDB = db.collection("post").document(postId).collection("comment")
                        .document(commentId)
                        .collection("reComment")
                    reCommentDB.document(reCommentData.commentId).set(reCommentData).await()
                    reCommentDB.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val data = mapOf(
                                "reCommentData" to task.result.toObjects(ReCommentDataResponse::class.java)
                            )
                            db.collection("post").document(postId).collection("comment")
                                .document(commentId).update(data)
                        } else {
                            Log.d("test_impl", "${task.exception}")
                        }
                    }
                }
                emit(true)
            } catch (e: Exception) {
                emit(false)
            }
        }
    }

    override suspend fun getReComment(
        postId: String,
        commentId: String
    ): Flow<List<ReCommentDataEntity>> {
        return callbackFlow {
            val reCommentData =
                db.collection("post").document(postId).collection("comment").document(commentId)
                    .collection("reComment")

            val snapShotListener = reCommentData.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList()).isSuccess
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val reCommentResponse = snapshot.toObjects(ReCommentDataResponse::class.java)
                    trySend(reCommentResponse.toReCommentListEntity()).isSuccess
                } else {
                    trySend(emptyList()).isSuccess
                }
            }
            awaitClose {
                snapShotListener.remove()
            }

        }
    }

    override suspend fun deleteReComment(
        postId: String,
        commentId: String,
        reCommentId: String
    ): Result<String> {
        return try {
            val reCommentDB =
                db.collection("post").document(postId).collection("comment").document(commentId)
                    .collection("reComment")
            reCommentDB.document(reCommentId).delete().await()
            reCommentDB.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val data =
                        mapOf("reCommentData" to task.result.toObjects(ReCommentDataResponse::class.java))
                    db.collection("post").document(postId).collection("comment").document(commentId)
                        .update(data)
                } else {
                    Log.d("test_impl", "${task.exception}")
                }
            }

            Result.success("success")
        } catch (e: Exception) {
            Result.failure(Exception("failure: ${e.message}"))
        }
    }

    override suspend fun getPagingPost(lastVisibleItem: Flow<Int>): Flow<List<PostEntity>?> {
        return callbackFlow {
        val posts = mutableListOf<PostEntity>()
        val postDetail = mutableListOf<DocumentSnapshot>()

            lastVisibleItem.collect { lastVisibleItem ->
                when (lastVisibleItem) {
                    0 -> {
                        db.collection("post").orderBy("createdAt", Query.Direction.DESCENDING)
                            .limit(3).get().addOnSuccessListener { postData ->
                                val document = postData.documents
                                val postResponse = postData.toObjects(PostDataResponse::class.java)
                                    .map { it.toEntity() }
                                postResponse.map { postEntity ->
                                    db.collection("user").document(postEntity.uid).get()
                                        .addOnSuccessListener { userData ->
                                            val userEntity =
                                                userData.toObject(UserDataResponse::class.java)
                                                    ?.toEntity()
                                            if (userEntity != null) {
                                                posts.addAll(listOf(PostEntity(userEntity, postEntity)))
                                            }
                                            postDetail.addAll(document)
                                            trySend(posts)
                                        }
                                }
                            }
                        Log.d("test_impl", "$posts")

                    }
                    posts.size -> {
                        db.collection("post")
                            .orderBy("createdAt", Query.Direction.DESCENDING)
                            .startAfter(postDetail.last()).limit(3)
                            .get().addOnSuccessListener { postData ->
                                val document = postData.documents
                                val postResponse =
                                    postData.toObjects(PostDataResponse::class.java)
                                        .map { it.toEntity() }
                                postResponse.map { postEntity ->
                                    db.collection("user").document(postEntity.uid).get()
                                        .addOnSuccessListener { userData ->
                                            val userEntity =
                                                userData.toObject(UserDataResponse::class.java)
                                                    ?.toEntity()
                                            if (userEntity != null) {
                                                posts.addAll(listOf(PostEntity(userEntity, postEntity)))
                                            }
                                            postDetail.addAll(document)
                                            trySend(posts)
                                        }
                                }
                            }
                    }
                    else -> {
                        trySend(null)
                    }
                }
                Log.d("test_post", "$posts")
            }
            awaitClose()
        }
    }
}


//    override suspend fun getTestPost(): Flow<List<TestModel>> {
//        return flow {
//            val posts = mutableListOf<TestModel>()
//            val postData =
//                db.collection("post").orderBy("createdAt", Query.Direction.DESCENDING).get().await()
//            val postDataResponse =
//                postData.toObjects(PostDataResponse::class.java).map { it.toEntity() }
//            postDataResponse.map {
//                db.collection("user").document(it.uid).get()
//                    .addOnSuccessListener { userData ->
//                        val userDataResponse =
//                            userData.toObject(UserDataResponse::class.java)?.toEntity()
//                        if (userDataResponse != null) {
//                            Log.d("test_impl", "$userDataResponse, $it")
//                            posts.add(TestModel(userDataResponse, it))
//                        }
//                    }
//
//        }
//        Log.d("test_impl", "$posts")
//        emit(posts)
//    }
//}