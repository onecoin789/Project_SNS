package com.example.project_sns.ui.view.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.GetPostByPostIdUseCase
import com.example.project_sns.domain.usecase.UpdateLikeUseCase
import com.example.project_sns.ui.mapper.toModel
import com.example.project_sns.ui.model.PostModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val getPostByPostIdUseCase: GetPostByPostIdUseCase,
    private val updateLikeUseCase: UpdateLikeUseCase
): ViewModel() {

    private val _likeResult = MutableLiveData<Boolean>()
    val likeResult: LiveData<Boolean> get() = _likeResult

    private val _postData = MutableLiveData<PostModel>()
    val postData: LiveData<PostModel> get() = _postData


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