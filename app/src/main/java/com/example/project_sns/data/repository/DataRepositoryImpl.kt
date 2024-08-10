package com.example.project_sns.data.repository

import androidx.core.net.toUri
import com.example.project_sns.data.mapper.toListEntity
import com.example.project_sns.data.response.PostDataResponse
import com.example.project_sns.domain.model.PostDataEntity
import com.example.project_sns.domain.repository.DataRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
                val storageRef =
                    storage.getReference("image").child("${currentUser}/${postData.postId}")
                if (currentUser != null) {
                    storageRef.putFile(postData.image.toUri()).addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener {
                            val downloadUri = it.toString()
                            val data = hashMapOf(
                                "uid" to currentUser,
                                "profileImage" to postData.profileImage,
                                "name" to postData.name,
                                "email" to postData.email,
                                "image" to downloadUri,
                                "postText" to postData.postText,
                                "lat" to postData.lat,
                                "lng" to postData.lng,
                                "createdAt" to postData.createdAt
                            )
                            db.collection("post").document(postData.postId).set(data)
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
}
