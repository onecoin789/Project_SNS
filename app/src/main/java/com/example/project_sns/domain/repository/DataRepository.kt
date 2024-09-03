package com.example.project_sns.domain.repository

import com.example.project_sns.domain.model.CommentDataEntity
import com.example.project_sns.domain.model.PostDataEntity
import com.example.project_sns.domain.model.ReCommentDataEntity
import com.example.project_sns.ui.view.model.ReCommentDataModel
import kotlinx.coroutines.flow.Flow


interface DataRepository {

    suspend fun uploadPost(postData: PostDataEntity): Flow<Boolean>

    suspend fun getCurrentUserPost(uid: String): Flow<List<PostDataEntity>>

    suspend fun getAllPost(): Flow<List<PostDataEntity>>

    suspend fun deletePost(postData: PostDataEntity?, commentList: List<CommentDataEntity>?): Result<String>

    suspend fun editPost(postData: PostDataEntity?): Flow<Boolean>

    suspend fun uploadComment(postId: String, commentData: CommentDataEntity?): Flow<Boolean>

    suspend fun getComment(postId: String): Flow<List<CommentDataEntity>>

    suspend fun deleteComment(postId: String, commentId: String, reCommentList: List<ReCommentDataEntity>?): Result<String>

    suspend fun uploadReComment(postId: String, commentId: String, reCommentData: ReCommentDataEntity?): Flow<Boolean>

    suspend fun getReComment(postId: String, commentId: String): Flow<List<ReCommentDataEntity>>

}