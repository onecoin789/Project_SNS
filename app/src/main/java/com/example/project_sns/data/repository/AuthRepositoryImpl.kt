package com.example.project_sns.data.repository

import android.util.Log
import androidx.core.net.toUri
import com.example.project_sns.data.mapper.toEntity
import com.example.project_sns.data.response.CurrentUserResponse
import com.example.project_sns.domain.model.CurrentUserEntity
import com.example.project_sns.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.storage.FirebaseStorage
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val functions: FirebaseFunctions
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
                                        trans.update(
                                            document.reference,
                                            "profileImage",
                                            downloadUri
                                        )
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

    override suspend fun kakaoLogin(accessToken: String): Result<String> {
        return try {
            val data = hashMapOf("token" to accessToken)
            val time = LocalDateTime.now()
            var value: Boolean? = true
            val createdAt = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            functions
                .getHttpsCallable("kakaoCustomAuth")
                .call(data)
                .addOnCompleteListener { task ->
                    val result = task.result?.data as HashMap<*, *>
                    var mKey: String? = null
                    for (key in result.keys) {
                        mKey = key.toString()
                    }
                    val customToken = result[mKey].toString()
                    auth.signInWithCustomToken(customToken).addOnCompleteListener { taskResult ->
                        if (taskResult.isSuccessful) {
                            value = false
                        }
                        if (value == false) {
                            UserApiClient.instance.me { user, error ->
                                if (error != null) {
                                    Log.d("user_info", "${error.message}")
                                } else if (user != null) {
                                    val uid = auth.currentUser?.uid
                                    val name = user.kakaoAccount?.profile?.nickname
                                    val email = "kakao${user.id}"
                                    if (uid != null) {
                                        db.collection("user").document(uid).get().addOnSuccessListener { dataCheck ->
                                            if (!dataCheck.exists()) {
                                                val kakaoData = hashMapOf(
                                                    "uid" to uid,
                                                    "name" to name,
                                                    "email" to email,
                                                    "profileImage" to null,
                                                    "intro" to "",
                                                    "createdAt" to createdAt
                                                )
                                                db.collection("user").document(uid).set(kakaoData)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            Result.success("Success")
        } catch (e: FirebaseAuthException) {
            Result.failure(Exception("FirebaseAuthException: ${e.message}"))
        } catch (e: FirebaseFunctionsException) {
            Result.failure(Exception("FirebaseFunctionsException: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Exception: ${e.message}"))
        }
    }
}