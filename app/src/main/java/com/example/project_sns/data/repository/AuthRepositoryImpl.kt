package com.example.project_sns.data.repository

import android.util.Log
import androidx.core.net.toUri
import com.example.project_sns.data.mapper.toEntity
import com.example.project_sns.data.mapper.toRequestDataEntity
import com.example.project_sns.data.response.FriendDataResponse
import com.example.project_sns.data.response.RequestDataResponse
import com.example.project_sns.data.response.UserDataResponse
import com.example.project_sns.data.response.toEntity
import com.example.project_sns.domain.entity.RequestEntity
import com.example.project_sns.domain.entity.UserDataEntity
import com.example.project_sns.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.storage.FirebaseStorage
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val COLLECTION_USER = "user"
private const val COLLECTION_POST = "post"
private const val COLLECTION_REQUEST = "request"
private const val COLLECTION_FRIEND_LIST = "friendList"

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
            val friendList = arrayListOf<String>()
            val friendData = hashMapOf("friendList" to friendList)

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
                            db.collection(COLLECTION_USER).document(user.uid).set(data)
                            db.collection(COLLECTION_FRIEND_LIST).document(user.uid).set(friendData)
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
                    db.collection(COLLECTION_USER).document(user.uid).set(data)
                    db.collection(COLLECTION_FRIEND_LIST).document(user.uid).set(friendData)
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

    override suspend fun getCurrentUserData(): Flow<UserDataEntity?> {
        return callbackFlow {
            val mAuth = auth.currentUser?.uid
            if (mAuth != null) {
                val docRef = db.collection(COLLECTION_USER).document(mAuth)
                val snapshotListener = docRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(null).isSuccess
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val userResponse = snapshot.toObject(UserDataResponse::class.java)
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

    override suspend fun getUserByUid(uid: String): Flow<UserDataEntity?> {
        return callbackFlow {
            val docRef = db.collection(COLLECTION_USER).document(uid)
            val snapshotListener = docRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null).isSuccess
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val userResponse = snapshot.toObject(UserDataResponse::class.java)
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
                        db.collection(COLLECTION_USER).document(uid).set(data)
                        db.collection(COLLECTION_POST).whereEqualTo("uid", uid).get()
                            .addOnSuccessListener { post ->
                                post.documents.forEach { document ->
                                    db.runTransaction { trans ->
                                        trans.update(
                                            document.reference,
                                            "profileImage",
                                            downloadUri
                                        )
                                        trans.update(document.reference, "name", name)
                                    }

                                    document.reference.collection("comment")
                                        .whereEqualTo("uid", uid).get()
                                        .addOnSuccessListener { comment ->
                                            comment.documents.forEach { commentDocument ->
                                                db.runTransaction { commentTrans ->
                                                    commentTrans.update(
                                                        commentDocument.reference,
                                                        "profileImage",
                                                        downloadUri
                                                    )
                                                    commentTrans.update(
                                                        commentDocument.reference,
                                                        "name",
                                                        name
                                                    )
                                                }
                                                commentDocument.reference.collection("reComment")
                                                    .whereEqualTo("uid", uid).get()
                                                    .addOnSuccessListener { reComment ->
                                                        reComment.documents.forEach { reCommentDocument ->
                                                            db.runTransaction { reCommentTrans ->
                                                                reCommentTrans.update(
                                                                    reCommentDocument.reference,
                                                                    "profileImage",
                                                                    downloadUri
                                                                )
                                                                reCommentTrans.update(
                                                                    reCommentDocument.reference,
                                                                    "name",
                                                                    name
                                                                )
                                                            }
                                                        }
                                                    }
                                            }
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
                db.collection(COLLECTION_USER).document(uid).set(data)
                db.collection(COLLECTION_POST).whereEqualTo("uid", uid).get()
                    .addOnSuccessListener { post ->
                        post.documents.forEach { document ->
                            db.runTransaction { trans ->
                                trans.update(document.reference, "name", name)
                            }
                            document.reference.collection("comment").whereEqualTo("uid", uid).get()
                                .addOnSuccessListener { comment ->
                                    comment.documents.forEach { commentDocument ->
                                        db.runTransaction { commentTrans ->
                                            commentTrans.update(
                                                commentDocument.reference,
                                                "name",
                                                name
                                            )
                                        }
                                        commentDocument.reference.collection("reComment")
                                            .whereEqualTo("uid", uid).get()
                                            .addOnSuccessListener { reComment ->
                                                reComment.documents.forEach { reCommentDocument ->
                                                    db.runTransaction { reCommentTrans ->
                                                        reCommentTrans.update(
                                                            reCommentDocument.reference,
                                                            "name",
                                                            name
                                                        )
                                                    }
                                                }
                                            }
                                    }
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
                                        db.collection(COLLECTION_USER).document(uid).get()
                                            .addOnSuccessListener { dataCheck ->
                                                if (!dataCheck.exists()) {
                                                    val kakaoData = hashMapOf(
                                                        "uid" to uid,
                                                        "name" to name,
                                                        "email" to email,
                                                        "profileImage" to null,
                                                        "intro" to "",
                                                        "createdAt" to createdAt
                                                    )
                                                    db.collection(COLLECTION_USER).document(uid)
                                                        .set(kakaoData)
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

    override suspend fun checkRequestList(): Flow<Boolean> {
        return callbackFlow {
            val currentUser = auth.currentUser?.uid
            db.collection(COLLECTION_REQUEST).whereEqualTo("toUid", currentUser).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(false)
                }
                if (snapshot != null) {
                   val document = snapshot.documents.size
                    if (document != 0) {
                        trySend(true)
                    } else {
                        trySend(false)
                    }
                }
            }
            awaitClose()
        }
    }

    override suspend fun requestFriend(
        requestId: String,
        fromUid: String,
        toUid: String
    ): Flow<Boolean> {
        return flow {
            try {
                val requestData = hashMapOf(
                    "requestId" to requestId,
                    "fromUid" to fromUid,
                    "toUid" to toUid
                )
                db.collection(COLLECTION_REQUEST).document(requestId).set(requestData)
                emit(true)
            } catch (e: Exception) {
                emit(false)
            }
        }
    }

    override suspend fun getRequestList(lastVisibleItem: Flow<Int>): Flow<List<RequestEntity>> {
        return callbackFlow {
            val currentUserUid = auth.currentUser?.uid
            val requestList = mutableListOf<RequestEntity>()
            val requestDocuments = mutableListOf<DocumentSnapshot>()
            val requestByUid =
                db.collection(COLLECTION_REQUEST).whereEqualTo("toUid", currentUserUid)

            lastVisibleItem.collect { lastVisibleItem ->
                when (lastVisibleItem) {
                    0 -> {
                        requestByUid.limit(10).addSnapshotListener { requestData, e ->
                            if (e != null) {
                                trySend(emptyList())
                            } else if (requestData != null) {
                                val documents = requestData.documents
                                val requestResponse =
                                    requestData.toObjects(RequestDataResponse::class.java)
                                        .toRequestDataEntity()
                                requestResponse.map { requestEntity ->
                                    db.collection(COLLECTION_USER).document(requestEntity.fromUid)
                                        .get()
                                        .addOnSuccessListener { userData ->
                                            val userEntity =
                                                userData.toObject(UserDataResponse::class.java)
                                                    ?.toEntity()
                                            if (userEntity != null) {
                                                requestList.addAll(
                                                    listOf(
                                                        RequestEntity(
                                                            requestId = requestEntity.requestId,
                                                            fromUid = userEntity,
                                                            toUid = requestEntity.toUid
                                                        )
                                                    )
                                                )
                                            }
                                            requestDocuments.addAll(documents)
                                            trySend(requestList)
                                        }
                                }
                            }
                        }
                    }

                    requestList.size -> {
                        requestByUid.startAfter(requestDocuments.last()).limit(10).addSnapshotListener { requestData, e ->
                            if (e != null) {
                                trySend(emptyList())
                            } else if (requestData != null) {
                                val documents = requestData.documents
                                val requestResponse =
                                    requestData.toObjects(RequestDataResponse::class.java)
                                        .toRequestDataEntity()
                                requestResponse.map { requestEntity ->
                                    db.collection(COLLECTION_USER).document(requestEntity.fromUid)
                                        .get()
                                        .addOnSuccessListener { userData ->
                                            val userEntity =
                                                userData.toObject(UserDataResponse::class.java)
                                                    ?.toEntity()
                                            if (userEntity != null) {
                                                requestList.addAll(
                                                    listOf(
                                                        RequestEntity(
                                                            requestId = requestEntity.requestId,
                                                            fromUid = userEntity,
                                                            toUid = requestEntity.toUid
                                                        )
                                                    )
                                                )
                                            }
                                            requestDocuments.addAll(documents)
                                            trySend(requestList)
                                        }
                                }
                            }
                        }
                    }
                    else -> {
                        Log.d("request_else", "request_else")
                    }
                }
            }
            awaitClose()
        }
    }

    override suspend fun acceptFriendRequest(
        requestId: String,
        fromUid: String,
        toUid: String
    ): Flow<Boolean> {
        return flow {
            try {
                db.runTransaction { transaction ->
                    val fromUidDoc = db.collection(COLLECTION_FRIEND_LIST).document(fromUid)
                    val toUidDoc = db.collection(COLLECTION_FRIEND_LIST).document(toUid)

                    transaction.update(
                        fromUidDoc,
                        "friendList",
                        FieldValue.arrayUnion(toUid)
                    )
                    transaction.update(
                        toUidDoc,
                        "friendList",
                        FieldValue.arrayUnion(fromUid)
                    )
                }.await()
                db.collection(COLLECTION_REQUEST).document(requestId).delete()
                emit(true)
            } catch (e: Exception) {
                Log.d("tag", "${e.message}")
                emit(false)
            }
        }
    }

    override suspend fun rejectFriendRequest(requestId: String): Flow<Boolean> {
        return flow {
            try {
                db.collection(COLLECTION_REQUEST).document(requestId).delete()
                emit(true)
            } catch (e: Exception) {
                emit(false)
            }
        }
    }

    override suspend fun checkFriendRequest(toUid: String): Flow<Boolean> {
        return callbackFlow {
            val fromUid = auth.currentUser?.uid
            val query = db.collection(COLLECTION_REQUEST).whereEqualTo("fromUid", fromUid)
                .whereEqualTo("toUid", toUid)
            val snapshotListener = query.addSnapshotListener { task, error ->
                if (error != null) {
                    trySend(false)
                }
                if (task != null) {
                    val document = task.documents
                    if (document.isNotEmpty()) {
                        Log.d("Tag_Success", "$document")
                        trySend(true)
                    } else {
                        Log.d("Tag_Fail", "$document")
                        trySend(false)
                    }
                }

            }
            awaitClose {
                snapshotListener.remove()
            }
        }
    }

    override suspend fun cancelFriendRequest(fromUid: String, toUid: String): Flow<Boolean> {
        return callbackFlow {
            val documentList = mutableListOf<RequestDataResponse>()
            val query = db.collection(COLLECTION_REQUEST).whereEqualTo("fromUid", fromUid)
                .whereEqualTo("toUid", toUid)
            query.get().addOnSuccessListener { requestResponse ->
                val document = requestResponse?.toObjects(RequestDataResponse::class.java)
                if (document != null) {
                    documentList.addAll(document)
                    val requestId = documentList.first().requestId
                    Log.d("tag_impl", "$requestId, $documentList")
                    db.collection(COLLECTION_REQUEST).document(requestId).delete()
                        .addOnSuccessListener {
                            documentList.clear()
                            trySend(true)
                        }
                } else {
                    trySend(false)
                }
            }
            awaitClose()
        }
    }


    override suspend fun getFriendList(uid: String): Flow<List<UserDataEntity>> {
        return callbackFlow {
            val friendList = mutableListOf<UserDataEntity>()
            db.collection(COLLECTION_FRIEND_LIST).document(uid)
                .addSnapshotListener { friendResponse, error ->
                    if (error != null) {
                        trySend(emptyList())
                    }
                    if (friendResponse != null) {
                        val friendEntity =
                            friendResponse.toObject(FriendDataResponse::class.java)?.toEntity()
                        if (friendEntity != null) {
                            friendEntity.friendList.map { friendUid ->
                                db.collection(COLLECTION_USER).document(friendUid)
                                    .addSnapshotListener { userData, error ->
                                        if (error != null) {
                                            trySend(emptyList())
                                        }
                                        if (userData != null) {
                                            val userEntity =
                                                userData.toObject(UserDataResponse::class.java)
                                                    ?.toEntity()
                                            if (userEntity != null) {
                                                friendList.addAll(listOf(userEntity))
                                                trySend(friendList)
                                            }
                                        }
                                    }
                            }
                        } else {
                            trySend(emptyList())
                        }
                    }
                }
            awaitClose()
        }
    }

    override suspend fun deleteFriend(fromUid: String, toUid: String): Flow<Boolean> {
        return flow {
            try {
                db.runTransaction { transaction ->
                    val fromUidDoc = db.collection(COLLECTION_FRIEND_LIST).document(fromUid)
                    val toUidDoc = db.collection(COLLECTION_FRIEND_LIST).document(toUid)
                    transaction.update(fromUidDoc, "friendList", FieldValue.arrayRemove(toUid))
                    transaction.update(toUidDoc, "friendList", FieldValue.arrayRemove(fromUid))
                }.await()
                emit(true)
            } catch (e: Exception) {
                emit(false)
            }
        }
    }
}