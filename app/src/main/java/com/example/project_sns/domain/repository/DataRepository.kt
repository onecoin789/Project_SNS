package com.example.project_sns.domain.repository

import android.net.Uri
import com.example.project_sns.domain.entity.ChatRoomDataEntity
import com.example.project_sns.domain.entity.ChatRoomEntity
import com.example.project_sns.domain.entity.CommentDataEntity
import com.example.project_sns.domain.entity.CommentEntity
import com.example.project_sns.domain.entity.MessageDataEntity
import com.example.project_sns.domain.entity.MessageEntity
import com.example.project_sns.domain.entity.PostDataEntity
import com.example.project_sns.domain.entity.PostEntity
import com.example.project_sns.domain.entity.ReCommentDataEntity
import com.example.project_sns.domain.entity.ReCommentEntity
import com.example.project_sns.domain.entity.UploadMessageDataEntity
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

    suspend fun checkChatRoom(recipientUid: String): Flow<Boolean>

    suspend fun getChatRoomData(recipientUid: String): Flow<ChatRoomDataEntity?>

    suspend fun sendFirstMessage(chatRoomId: String, senderUid: String, recipientUid: String, messageData: UploadMessageDataEntity): Flow<Boolean>

    suspend fun sendMessage(chatRoomId: String,token: String, recipientUser: String, accessToken: String, messageData: UploadMessageDataEntity): Flow<Boolean>

    suspend fun sendImageMessage(chatRoomId: String, chatImageList: List<Uri>): Flow<Boolean>

    suspend fun getChatRoomList(): Flow<List<ChatRoomEntity>>

    suspend fun checkMessageData(chatRoomId: String): Flow<Boolean>

    suspend fun getChatMessageData(chatRoomId: String, lastVisibleItem: Flow<Int>): Flow<List<MessageEntity>>

}