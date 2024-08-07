package com.example.project_sns.data.repository

import android.util.Log
import androidx.core.net.toUri
import com.example.project_sns.data.mapper.toEntity
import com.example.project_sns.data.response.CurrentUserResponse
import com.example.project_sns.domain.model.CurrentUserEntity
import com.example.project_sns.domain.repository.AuthRepository
import com.example.project_sns.ui.util.dateFormat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
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
        imageUri: String?
    ): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            val storageRef = storage.getReference("image").child("${user?.uid}/profileImage")
            val time = LocalDateTime.now()

            if (user != null) {
                if (imageUri != null) {
                    storageRef.putFile(imageUri.toUri()).addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener {
                            val downloadUri = it.toString()
                            val data = hashMapOf(
                                "uid" to user.uid,
                                "name" to name,
                                "email" to email,
                                "profileImage" to downloadUri,
                                "createdAt" to dateFormat(time)
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
                        "createdAt" to dateFormat(time)
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
        return flow {
            val mAuth = auth.currentUser?.uid
            if (mAuth != null) {
                val docRef = db.collection("user").document(mAuth).get().await()
                val userResponse = docRef.toObject(CurrentUserResponse::class.java)
                Log.d("userdata_impl", "${docRef}, ${userResponse}")
                if (userResponse != null) {
                    emit(userResponse.toEntity())
                } else {
                    throw NullPointerException("Userdata is Null")
                }
            }
        }.catch { exception ->
            throw exception
        }
    }

    override suspend fun editProfile(): Flow<Boolean> {
        return flow {

        }
    }
}