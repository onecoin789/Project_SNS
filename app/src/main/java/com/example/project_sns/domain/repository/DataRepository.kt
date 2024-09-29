package com.example.project_sns.domain.repository

import com.example.project_sns.domain.model.CommentDataEntity
import com.example.project_sns.domain.model.CommentEntity
import com.example.project_sns.domain.model.PostDataEntity
import com.example.project_sns.domain.model.PostEntity
import com.example.project_sns.domain.model.ReCommentDataEntity
import com.example.project_sns.domain.model.ReCommentEntity
import com.example.project_sns.domain.model.UserDataEntity
import kotlinx.coroutines.flow.Flow


interface DataRepository {

    suspend fun uploadPost(postData: PostDataEntity): Flow<Boolean>

    suspend fun getCurrentUserPost(uid: String): Flow<List<PostDataEntity>>

    suspend fun getAllPost(): Flow<List<PostDataEntity>>

    suspend fun editPost(postData: PostDataEntity?): Flow<Boolean>

    suspend fun deletePost(postData: PostDataEntity?, commentList: List<CommentDataEntity>?): Result<String>

    suspend fun uploadComment(commentData: CommentDataEntity?): Flow<Boolean>

    suspend fun getComment(postId: String, lastVisibleItem: Flow<Int>): Flow<List<CommentEntity>>

    suspend fun deleteComment(commentId: String): Result<String>

    suspend fun uploadReComment(reCommentData: ReCommentDataEntity?): Flow<Boolean>

    suspend fun getReComment(commentId: String, lastVisibleItem: Flow<Int>): Flow<List<ReCommentEntity>>

    suspend fun deleteReComment(commentId: String, reCommentId: String): Result<String>

    suspend fun getPagingPost(lastVisibleItem: Flow<Int>): Flow<List<PostEntity>?>

}