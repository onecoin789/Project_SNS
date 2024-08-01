package com.example.project_sns.data.repository

import android.net.Uri
import android.util.Log
import android.util.LogPrinter
import androidx.core.net.toUri
import com.example.project_sns.data.mapper.toEntity
import com.example.project_sns.data.response.CurrentUserResponse
import com.example.project_sns.domain.model.CurrentUserEntity
import com.example.project_sns.domain.repository.AuthRepository
import com.example.project_sns.ui.view.signup.model.FirebaseUserData
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
        imageUri: String
    ): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                setData(uid = user.uid, name = name,email = email, imageUri = imageUri)
//                db.collection("user").document(user.uid).set(data)
//                    .addOnSuccessListener {
//                        Log.d("debug_signup", "success")
//                    }
//                    .addOnFailureListener {
//                        Log.d("debug_signup", "fail")
//                    }
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

    private fun setData(uid: String, name: String, email: String, imageUri: String) {

        val storageRef = storage.getReference(uid).child("${uid}.profileImage")
        val data = FirebaseUserData(uid = uid, name = name, email = email, profileImage = imageUri, createdAt = Timestamp.now())

        db.collection("user").document(uid).set(data)
            .addOnSuccessListener {
                Log.d("debug_signup", "success")
            }
            .addOnFailureListener {
                Log.d("debug_signup", "fail")
            }

        storageRef.putFile(imageUri.toUri())
            .addOnSuccessListener {
                Log.d("debug_signup", "success")
            }.addOnFailureListener {
                Log.d("debug_signup", "fail")
            }

    }
}