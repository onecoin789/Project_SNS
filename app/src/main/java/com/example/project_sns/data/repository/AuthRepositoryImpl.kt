package com.example.project_sns.data.repository

import androidx.core.net.toUri
import com.example.project_sns.data.mapper.toEntity
import com.example.project_sns.data.response.CurrentUserResponse
import com.example.project_sns.domain.model.CurrentUserEntity
import com.example.project_sns.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : AuthRepository {
    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
        profileImage: String?,
        createdAt: String
    ): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            val storageRef = storage.getReference("image").child("${user?.uid}/profileImage")

            if (user != null) {
                if (profileImage != null) {
                    storageRef.putFile(profileImage.toUri()).addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            val downloadUri = uri.toString()
                            val data = hashMapOf(
                                "uid" to user.uid,
                                "name" to name,
                                "email" to email,
                                "profileImage" to downloadUri,
                                "createdAt" to createdAt
                            )
                            db.collection("user").document(user.uid).set(data)
                        }
                    }
                } else {
                    val data = hashMapOf(
                        "uid" to user.uid,
                        "name" to name,
                        "email" to email,
                        "profileImage" to null,
                        "createdAt" to createdAt
                    )
                    db.collection("user").document(user.uid).set(data)
                }
                Result.success("Success")
            } else {
                Result.failure(Exception("Fail"))
            }
        } catch (e: FirebaseAuthException) {
            Result.failure(Exception("FirebaseAuthException: ${e.message}"))
        } catch (e: FirebaseFirestoreException) {
            Result.failure(Exception("FirebaseFirestoreException: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Exception: ${e.message}"))
        }
    }

    override suspend fun logIn(email: String, password: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success("success")
        } catch (e: Exception) {
            return Result.failure(Exception("Exception: ${e.message}"))
        }

    }

    override suspend fun logout(): Result<String> {
        return try {
            auth.signOut()
            Result.success("success")
        } catch (e: Exception) {
            return Result.failure(Exception("Exception: ${e.message}"))
        }
    }

    override suspend fun getCurrentUserData(): Flow<CurrentUserEntity?> {
        return callbackFlow {
            val mAuth = auth.currentUser?.uid
            if (mAuth != null) {
                val docRef = db.collection("user").document(mAuth)
                val snapshotListener = docRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(null).isSuccess
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val userResponse = snapshot.toObject(CurrentUserResponse::class.java)
                        trySend(userResponse?.toEntity()).isSuccess
                    } else {
                        trySend(null).isSuccess
                    }
                }
                awaitClose {
                    snapshotListener.remove()
                }
            }
        }
    }

    override suspend fun editProfile(
        uid: String,
        name: String,
        email: String,
        newProfile: String?,
        beforeProfile: String?,
        intro: String?,
        createdAt: String
    ): Result<String> {
        return try {
            val storageRef = storage.getReference("image").child("${uid}/profileImage")
            if (newProfile != null) {
                storageRef.putFile(newProfile.toUri()).addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUri = uri.toString()
                        val data = hashMapOf(
                            "uid" to uid,
                            "name" to name,
                            "email" to email,
                            "intro" to intro,
                            "profileImage" to downloadUri,
                            "createdAt" to createdAt
                        )
                        db.collection("user").document(uid).set(data)
                        db.collection("post").whereEqualTo("uid", uid).get()
                            .addOnSuccessListener {
                                it.documents.forEach { document ->
                                    db.runTransaction { trans ->
                                        trans.update(document.reference, "profileImage", downloadUri)
                                        trans.update(document.reference, "name", name)
                                    }
                                }
                            }
                    }
                }
            } else {
                val data = hashMapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "intro" to intro,
                    "profileImage" to beforeProfile,
                    "createdAt" to createdAt
                )
                db.collection("user").document(uid).set(data)
                db.collection("post").whereEqualTo("uid", uid).get()
                    .addOnSuccessListener {
                        it.documents.forEach { document ->
                            db.runTransaction { trans ->
                                trans.update(document.reference, "name", name)
                            }
                        }
                    }
            }
            Result.success("success")
        } catch (e: Exception) {
            Result.failure(Exception("Exception: ${e.message}"))
        }
    }
}