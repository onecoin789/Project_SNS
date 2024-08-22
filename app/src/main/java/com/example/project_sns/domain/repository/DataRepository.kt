package com.example.project_sns.domain.repository

import com.example.project_sns.domain.model.CommentDataEntity
import com.example.project_sns.domain.model.PostDataEntity
import kotlinx.coroutines.flow.Flow


interface DataRepository {

    suspend fun uploadPost(postData: PostDataEntity): Flow<Boolean>

    suspend fun getCurrentUserPost(uid: String): Flow<List<PostDataEntity>>

    suspend fun getAllPost(): Flow<List<PostDataEntity>>

    suspend fun deletePost(postData: PostDataEntity?): Result<String>

    suspend fun editPost(postData: PostDataEntity?): Flow<Boolean>

    suspend fun uploadComment(): Flow<CommentDataEntity?>

}