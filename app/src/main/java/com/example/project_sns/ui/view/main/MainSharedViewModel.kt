package com.example.project_sns.ui.view.main

import android.util.Log
import android.util.Printer
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.DeleteCommentUseCase
import com.example.project_sns.domain.usecase.DeletePostUseCase
import com.example.project_sns.domain.usecase.DeleteReCommentUseCase
import com.example.project_sns.domain.usecase.EditPostUseCase
import com.example.project_sns.domain.usecase.EditProfileUseCase
import com.example.project_sns.domain.usecase.GetCommentDataUseCase
import com.example.project_sns.domain.usecase.GetCurrentUserPostDataUseCase
import com.example.project_sns.domain.usecase.GetFriendListDataUseCase
import com.example.project_sns.domain.usecase.GetReCommentDataUseCase
import com.example.project_sns.domain.usecase.GetRequestDataUseCase
import com.example.project_sns.domain.usecase.GetUserByUidUseCase
import com.example.project_sns.domain.usecase.SearchKakaoMapUseCase
import com.example.project_sns.domain.usecase.SendFriendRequestUseCase
import com.example.project_sns.domain.usecase.UploadCommentUseCase
import com.example.project_sns.domain.usecase.UploadPostUseCase
import com.example.project_sns.domain.usecase.UploadReCommentUseCase
import com.example.project_sns.ui.CurrentPost
import com.example.project_sns.ui.mapper.toCommentListEntity
import com.example.project_sns.ui.mapper.toCommentListModel
import com.example.project_sns.ui.mapper.toEntity
import com.example.project_sns.ui.mapper.toKakaoListEntity
import com.example.project_sns.ui.mapper.toModel
import com.example.project_sns.ui.mapper.toPostListModel
import com.example.project_sns.ui.mapper.toReCommentListModel
import com.example.project_sns.ui.mapper.toRequestDataModel
import com.example.project_sns.ui.mapper.toUserDataListModel
import com.example.project_sns.ui.util.CheckEditProfile
import com.example.project_sns.ui.view.model.CommentDataModel
import com.example.project_sns.ui.view.model.CommentModel
import com.example.project_sns.ui.view.model.FriendDataModel
import com.example.project_sns.ui.view.model.KakaoDocumentsModel
import com.example.project_sns.ui.view.model.PostDataModel
import com.example.project_sns.ui.view.model.PostModel
import com.example.project_sns.ui.view.model.ReCommentDataModel
import com.example.project_sns.ui.view.model.ReCommentModel
import com.example.project_sns.ui.view.model.RequestDataModel
import com.example.project_sns.ui.view.model.RequestModel
import com.example.project_sns.ui.view.model.UserDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class MainSharedViewModel @Inject constructor(
    private val uploadPostUseCase: UploadPostUseCase,
    private val getCurrentUserPostDataUseCase: GetCurrentUserPostDataUseCase,
    private val editProfileUseCase: EditProfileUseCase,
    private val searchKakaoMapUseCase: SearchKakaoMapUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val editPostUseCase: EditPostUseCase,
    private val uploadCommentUseCase: UploadCommentUseCase,
    private val getCommentDataUseCase: GetCommentDataUseCase,
    private val uploadReCommentUseCase: UploadReCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val getReCommentDataUseCase: GetReCommentDataUseCase,
    private val deleteReCommentUseCase: DeleteReCommentUseCase,
    private val getUserByUidUseCase: GetUserByUidUseCase,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    private val getRequestDataUseCase: GetRequestDataUseCase,
    private val getFriendListDataUseCase: GetFriendListDataUseCase
) : ViewModel() {

    private val _postUpLoadResult = MutableStateFlow<Boolean?>(null)
    val postUpLoadResult: StateFlow<Boolean?> get() = _postUpLoadResult

    private val _editEvent = Channel<CheckEditProfile?> { }
    val editEvent = _editEvent.receiveAsFlow()

    private val _userData = MutableLiveData<UserDataModel?>()
    val userData: LiveData<UserDataModel?> get() = _userData

    private val _postList = MutableStateFlow<List<PostDataModel>>(emptyList())
    val postList: StateFlow<List<PostDataModel>> get() = _postList

    private val _postData = MutableLiveData<PostDataModel?>()
    val postData: LiveData<PostDataModel?> get() = _postData

    private val _mapList = MutableStateFlow<List<KakaoDocumentsModel>>(emptyList())
    val mapList: StateFlow<List<KakaoDocumentsModel>> get() = _mapList

    private val _selectedCommentData = MutableLiveData<CommentModel?>()
    val selectedCommentData: LiveData<CommentModel?> get() = _selectedCommentData

    private val _selectedReCommentData = MutableLiveData<ReCommentDataModel?>()
    val selectedReCommentData: LiveData<ReCommentDataModel?> get() = _selectedReCommentData

    private val _postEditResult = MutableStateFlow<Boolean?>(null)
    val postEditResult: StateFlow<Boolean?> get() = _postEditResult

    private val _commentData = MutableStateFlow<Boolean?>(null)
    val commentData: StateFlow<Boolean?> get() = _commentData

    private val _commentListData = MutableLiveData<List<CommentModel>>(emptyList())
    val commentListData: LiveData<List<CommentModel>> get() = _commentListData

    private val _reCommentData = MutableStateFlow<Boolean?>(null)
    val reCommentData: StateFlow<Boolean?> get() = _reCommentData

    private val _reCommentListData = MutableLiveData<List<ReCommentModel>>(emptyList())
    val reCommentListData: LiveData<List<ReCommentModel>> get() = _reCommentListData

    private val _currentPage = MutableLiveData<Int>(0)
    val currentPage: LiveData<Int> get() = _currentPage

    private val _requestFriendResult = MutableStateFlow<Boolean?>(null)
    val requestFriendResult: StateFlow<Boolean?> get() = _requestFriendResult

    private val _friendList = MutableLiveData<List<UserDataModel>>()
    val friendList: LiveData<List<UserDataModel>> get() = _friendList


    val reCommentLastVisibleItem = MutableStateFlow(0)




    fun getFriendList(uid: String) {
        viewModelScope.launch {
            getFriendListDataUseCase(uid).collect { data ->
                _friendList.value = data.toUserDataListModel()
                Log.d("test_vm", "${_friendList.value}")
            }
        }
    }

    fun resetCommentData() {
        viewModelScope.launch {
            _commentListData.value = emptyList()
        }
    }

    fun resetReCommentData() {
        viewModelScope.launch {
            _reCommentListData.value = emptyList()
        }
    }

    fun startPage() {
        if (_currentPage.value != 0) {
            _currentPage.value = 0
        }
    }

    fun nextPage() {
        _currentPage.value = _currentPage.value?.plus(1)
    }

    fun prevPage() {
        _currentPage.value = _currentPage.value?.minus(1)
    }


    fun requestFriend(requestId: String, sendUid: String, receiveUid: String) {
        viewModelScope.launch {
            sendFriendRequestUseCase(requestId, sendUid, receiveUid).collect { result ->
                _requestFriendResult.value = result
            }
        }
    }

    fun uploadReComment(reCommentData: ReCommentDataModel?) {
        viewModelScope.launch {
            if (reCommentData != null) {
                uploadReCommentUseCase(reCommentData.toEntity()).collect { result ->
                    _reCommentData.value = result
                }
            }
        }
    }


    fun uploadPostData(postData: PostDataModel) {
        viewModelScope.launch {
            uploadPostUseCase(postData.toEntity()).collect { result ->
                _postUpLoadResult.value = result
            }
        }
    }

    fun getUserPost(uid: String) {
        viewModelScope.launch {
            getCurrentUserPostDataUseCase(uid).collect {
                val postList = it.toPostListModel()
                _postList.value = postList
            }
        }
    }

    fun getUserData(uid: String) {
        viewModelScope.launch {
            getUserByUidUseCase(uid).collect { data ->
                if (data != null) {
                    _userData.value = data.toModel()
                }
            }
        }
    }

    fun uploadComment(commentData: CommentDataModel?) {
        viewModelScope.launch {
            uploadCommentUseCase(commentData?.toEntity()).collect { result ->
                _commentData.value = result
            }
        }
    }

    fun getComment(postId: String, lastVisibleItem: Flow<Int>) {
        viewModelScope.launch {
            getCommentDataUseCase(postId, lastVisibleItem).collect {
                val commentList = it.toCommentListModel()
                _commentListData.value = commentList
                Log.d("test_vm", "${_commentListData.value}")
            }
        }
    }

    fun getReComment(commentId: String, lastVisibleItem: Flow<Int>) {
        viewModelScope.launch {
            getReCommentDataUseCase(commentId, lastVisibleItem).collect {
                val reCommentList = it.toReCommentListModel()
                _reCommentListData.value = reCommentList
                CurrentPost.reCommentList = it.toReCommentListModel()
            }
        }
    }

    fun checkName(name: String, item: TextView): Boolean {
        val nameText = name.trim()
        val namePattern = Pattern.matches("^[ㄱ-ㅣ가-힣a-zA-Z\\s]+$", nameText)
        if (namePattern) {
            item.visibility = View.INVISIBLE
            return true
        } else {
            item.visibility = View.VISIBLE
            return false
        }
    }

    private fun nullCheck(text: String): Boolean {
        return text.isEmpty()
    }

    private fun editProfile(
        uid: String,
        name: String,
        email: String,
        newProfile: String?,
        beforeProfile: String?,
        intro: String?,
        createdAt: String
    ) {
        viewModelScope.launch {
            val editData = editProfileUseCase(
                uid = uid, name = name, email = email,
                newProfile = newProfile,
                beforeProfile = beforeProfile,
                intro = intro,
                createdAt = createdAt
            )
            if (editData.isSuccess) {
                _editEvent.send(CheckEditProfile.EditSuccess)
            } else {
                _editEvent.send(CheckEditProfile.EditFail("유효하지 않은 이름입니다."))
            }
        }
    }

    fun checkEdit(
        uid: String,
        name: String,
        email: String,
        newProfile: String?,
        beforeProfile: String?,
        intro: String?,
        createdAt: String,
        nameCheck: TextView
    ) {
        if (nullCheck(name)) {
            viewModelScope.launch {
                _editEvent.send(CheckEditProfile.EditFail("이름을 입력해주세요."))
            }
            return
        }
        if (!checkName(name, nameCheck)) {
            viewModelScope.launch {
                _editEvent.send(CheckEditProfile.EditFail("유효하지 않은 이름입니다."))
            }
        } else {
            editProfile(uid, name, email, newProfile, beforeProfile, intro, createdAt)
        }
    }

    fun searchMapList(query: String, size: Int, page: Int) {
        viewModelScope.launch {
            searchKakaoMapUseCase(query = query, size = size, page = page).collect { data ->
                if (data != null) {
                    _mapList.value = data.documents.toKakaoListEntity()
                }
            }
        }
    }

    fun getPostData(data: PostDataModel?) {
        viewModelScope.launch {
            if (data != null) {
                _postData.value = data
                CurrentPost.postData = data
            }
        }
    }

    fun getCommentData(data: CommentModel?) {
        viewModelScope.launch {
            if (data != null) {
                _selectedCommentData.value = data
            }
        }
    }

    fun getReCommentData(data: ReCommentDataModel?) {
        viewModelScope.launch {
            if (data != null) {
                _selectedReCommentData.value = data
            }
        }
    }


    fun deletePost(postData: PostDataModel?, commentList: List<CommentDataModel>?) {
        viewModelScope.launch {
            deletePostUseCase(postData?.toEntity(), commentList?.toCommentListEntity())
        }
    }

    fun deleteComment(postId: String) {
        viewModelScope.launch {
            deleteCommentUseCase(postId)
        }
    }

    fun deleteReComment(commentId: String, reCommentId: String) {
        viewModelScope.launch {
            deleteReCommentUseCase(commentId, reCommentId)
        }
    }

    fun editPost(postData: PostDataModel?) {
        viewModelScope.launch {
            editPostUseCase(postData?.toEntity()).collect { result ->
                _postEditResult.value = result
            }
        }
    }

}