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
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                db.collection("user").document(user.uid).set(data)
                    .addOnSuccessListener {
                        Log.d("debug_signup", "success")
                    }
                    .addOnFailureListener {
                        Log.d("debug_signup", "fail")
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
            Result.success("Success")
        } catch (e: Exception) {
            return Result.failure(Exception("Exception: ${e.message}"))
        }

    }
}