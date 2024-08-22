package com.example.project_sns.data.repository

import android.util.Log
import androidx.core.net.toUri
import com.example.project_sns.data.mapper.toListEntity
import com.example.project_sns.data.response.PostDataResponse
import com.example.project_sns.domain.model.CommentDataEntity
import com.example.project_sns.domain.model.PostDataEntity
import com.example.project_sns.domain.repository.DataRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DataRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : DataRepository {
    override suspend fun uploadPost(postData: PostDataEntity): Flow<Boolean> {
        return flow {
            try {
                val currentUser = auth.currentUser?.uid
                val imageList = arrayListOf<String>()
                if (currentUser != null) {
                    val data = hashMapOf(
                        "postId" to postData.postId,
                        "uid" to currentUser,
                        "profileImage" to postData.profileImage,
                        "name" to postData.name,
                        "email" to postData.email,
                        "postText" to postData.postText,
                        "mapData" to postData.mapData,
                        "createdAt" to postData.createdAt
                    )
                    db.collection("post").document(postData.postId).set(data)
                        .addOnCompleteListener { task ->
                            val success = task.isSuccessful
                            Log.d("task_upload", "${success}")
                            val fail = task.isCanceled
                            Log.d("task_upload", "${fail}")

                        }

                    if (postData.image != null) {
                        for (i in 0 until postData.image.count()) {
                            val storageRef =
                                storage.getReference("image")
                                    .child("${currentUser}/${postData.postId}/${postData.createdAt}_${i}")
                            storageRef.putFile(postData.image[i].toUri()).addOnSuccessListener {
                                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                    imageList.add(downloadUrl.toString())
                                    val imageData =
                                        hashMapOf("image" to imageList.sorted())
                                    db.collection("post").document(postData.postId)
                                        .set(imageData, SetOptions.merge())
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
                val postData = db.collection("post").whereEqualTo("uid", uid)
                val snapShotListener = postData.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(emptyList()).isSuccess
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val postResponse = snapshot.toObjects(PostDataResponse::class.java)
                        trySend(postResponse.toListEntity()).isSuccess
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
            val snapshotListener = db.collection("post").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList()).isSuccess
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val postResponse = snapshot.toObjects(PostDataResponse::class.java)
                    trySend(postResponse.toListEntity()).isSuccess
                } else {
                    trySend(emptyList()).isSuccess
                }
            }
            awaitClose {
                snapshotListener.remove()
            }
        }
    }

    override suspend fun deletePost(postData: PostDataEntity?): Result<String> {
        return try {
            val uid = auth.currentUser?.uid
            if (postData != null) {
                val db = db.collection("post").document(postData.postId)

                if (postData.image != null) {
                    for (i in 0 until postData.image.count()) {
                        val storage = storage.getReference("image")
                            .child("${uid}/${postData.postId}/${postData.createdAt}_${i}")
                        db.delete()
                        storage.delete()
                    }
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
                val imageList = arrayListOf<String>()

                if (postData != null) {
                    db.collection("post").document(postData.postId).set(postData)
                    if (postData.image != null)
                        for (i in 0 until postData.image.count()) {
                            val storageRef = storage.getReference("image")
                                .child("${uid}/${postData.postId}/${postData.createdAt}_${i}")
                            storageRef.putFile(postData.image[i].toUri()).addOnSuccessListener {
                                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                    imageList.add(downloadUri.toString())
                                    val imageData = mapOf("image" to imageList.sorted())
                                    db.collection("post").document(postData.postId)
                                        .update(imageData)
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


    override suspend fun uploadComment(): Flow<CommentDataEntity?> {
        TODO()
//        return flow {
//            try {
//                val currentUser = auth.currentUser?.uid
//                val storage = storage.getReference("image").child()
//            }
//        }
    }
}
