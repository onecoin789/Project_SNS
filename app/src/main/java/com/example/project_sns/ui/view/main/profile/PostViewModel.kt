package com.example.project_sns.ui.view.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Priority
import com.example.project_sns.domain.usecase.GetLikeUserDataUseCase
import com.example.project_sns.domain.usecase.GetPostByPostIdUseCase
import com.example.project_sns.domain.usecase.UpdateLikeUseCase
import com.example.project_sns.ui.mapper.toModel
import com.example.project_sns.ui.mapper.toUserDataListModel
import com.example.project_sns.ui.model.PostModel
import com.example.project_sns.ui.model.UserDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val getPostByPostIdUseCase: GetPostByPostIdUseCase,
    private val updateLikeUseCase: UpdateLikeUseCase,
    private val getLikeUserDataUseCase: GetLikeUserDataUseCase
): ViewModel() {

    private val _likeResult = MutableLiveData<Boolean>()
    val likeResult: LiveData<Boolean> get() = _likeResult

    private val _postData = MutableLiveData<PostModel>()
    val postData: LiveData<PostModel> get() = _postData

    private val _likeUserList = MutableLiveData<List<UserDataModel>>()
    val likeUserList: LiveData<List<UserDataModel>> get() = _likeUserList


    fun getLikeUserData(postId: String) {
        viewModelScope.launch {
            getLikeUserDataUseCase(postId).collect { userList ->
                _likeUserList.value = userList.toUserDataListModel()
            }
        }
    }


    fun updateLike(chatRoomId: String, likeValue: Boolean) {
        viewModelScope.launch {
            updateLikeUseCase(chatRoomId, likeValue).collect { result ->
                _likeResult.value = result
            }
        }
    }

    fun getPostDataByPostId(postId: String) {
        viewModelScope.launch {
            getPostByPostIdUseCase(postId).collect { postData ->
                if (postData != null) {
                    val postModel = postData.toModel()
                    _postData.value = postModel
                }
            }
        }
    }
}