package com.example.project_sns.data.repository

import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.project_sns.domain.repository.AuthRepository
import com.example.project_sns.ui.view.signup.data.FirebaseUserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : AuthRepository {
    override suspend fun signUp(
        email: String,
        password: String,
        data: FirebaseUserData
    ): Result<String> {
        return try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        db.collection("user").document(data.email).set(data)
                            .addOnSuccessListener {
                                Log.d("debug_signup", "success")
                            }
                            .addOnFailureListener {
                                Log.d("debug_signup", "fail")
                            }
                    } else {
                        try {
                            it.result
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            auth.signOut()
            Result.success("Success")

        } catch (e: FirebaseAuthException) {
            Result.failure(Exception("FirebaseAuthException: ${e.message}"))
        } catch (e: FirebaseFirestoreException) {
            Result.failure(Exception("FirebaseFirestoreException: ${e.message}"))
        }
    }

    override suspend fun logIn(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Log.d("debug_repo_login", "success")
                }
                .addOnFailureListener {
                    Log.d("debug_repo_login", "fail")
                }
        } catch (e: Exception) {
            Log.d("debug_login", "로그인 에러")
        }
    }

}