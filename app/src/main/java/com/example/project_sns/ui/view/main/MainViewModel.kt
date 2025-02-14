package com.example.project_sns.ui.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.CheckFriendListUseCase
import com.example.project_sns.domain.usecase.EditPostUseCase
import com.example.project_sns.domain.usecase.GetAllPostUseCase
import com.example.project_sns.domain.usecase.GetCurrentUserDataUseCase
import com.example.project_sns.domain.usecase.GetCurrentUserPostDataUseCase
import com.example.project_sns.domain.usecase.GetFriendListDataUseCase
import com.example.project_sns.domain.usecase.GetPagingPostUseCase
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.mapper.toEntity
import com.example.project_sns.ui.mapper.toModel
import com.example.project_sns.ui.mapper.toPostDataListModel
import com.example.project_sns.ui.mapper.toPostListModel
import com.example.project_sns.ui.mapper.toUserDataListModel
import com.example.project_sns.ui.model.PostDataModel
import com.example.project_sns.ui.model.PostModel
import com.example.project_sns.ui.model.UserDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentUserDataUseCase: GetCurrentUserDataUseCase,
    private val getAllPostUseCase: GetAllPostUseCase,
    private val editPostUseCase: EditPostUseCase,
    private val getPagingPostUseCase: GetPagingPostUseCase,
    private val getFriendListDataUseCase: GetFriendListDataUseCase,
    private val getCurrentUserPostDataUseCase: GetCurrentUserPostDataUseCase,
    private val checkFriendListUseCase: CheckFriendListUseCase
) : ViewModel() {

    private val _currentUserData = MutableLiveData<UserDataModel?>(null)
    val currentUserData: LiveData<UserDataModel?> get() = _currentUserData

    private val _allPostData = MutableStateFlow<List<PostDataModel>>(emptyList())
    val allPostData: StateFlow<List<PostDataModel>> get() = _allPostData

    private val _checkFriendListResult = MutableLiveData<Boolean>()
    val checkFriendListResult: LiveData<Boolean> get() = _checkFriendListResult

    private val _friendList = MutableLiveData<List<UserDataModel>>()
    val friendList: LiveData<List<UserDataModel>> get() = _friendList

    //main post 관련

    private val _pagingData = MutableLiveData<List<PostModel>>(emptyList())
    val pagingData: LiveData<List<PostModel>> get() = _pagingData

    private val _postLastVisibleItem = MutableStateFlow<Int>(0)
    val postLastVisibleItem: StateFlow<Int> get() = _postLastVisibleItem


    //get post

    private val _postList = MutableLiveData<List<PostDataModel>>()
    val postList: LiveData<List<PostDataModel>> get() = _postList

    fun getUserPost(uid: String) {
        viewModelScope.launch {
            getCurrentUserPostDataUseCase(uid).collect {
                val postList = it.toPostListModel()
                _postList.value = postList
            }
        }
    }

    //edit post

    private val _postEditResult = MutableStateFlow<Boolean?>(null)
    val postEditResult: StateFlow<Boolean?> get() = _postEditResult


    fun getPagingData(lastVisibleItem: Flow<Int>) {
        viewModelScope.launch {
            getPagingPostUseCase(lastVisibleItem).collect { data ->
                _pagingData.value = data?.toPostDataListModel()
            }
        }
    }

    fun postLastVisibleItem(lastVisibleItem: Int) {
        viewModelScope.launch {
            _postLastVisibleItem.value = lastVisibleItem
        }
    }

    fun resetPagingData() {
        viewModelScope.launch {
            _pagingData.value = emptyList()
        }
    }

    fun editPost(postData: PostDataModel?) {
        viewModelScope.launch {
            editPostUseCase(postData?.toEntity()).collect { result ->
                _postEditResult.value = result
            }
        }
    }

    fun checkFriendList() {
        viewModelScope.launch {
            checkFriendListUseCase().collect { result ->
                _checkFriendListResult.value = result
            }
        }
    }

    fun getFriendList(uid: String) {
//        _friendList.value = emptyList()
        viewModelScope.launch {
            getFriendListDataUseCase(uid).collect { data ->
                _friendList.value = data.toUserDataListModel()
            }
        }
    }


//    fun getPagingData(lastVisibleItem: Flow<Int>) {
//        viewModelScope.launch {
//            getPagingPostUseCase(lastVisibleItem).collect { data ->
//                _pagingData.value = data?.toPostDataListModel()
//            }
//        }
//    }
//
//    fun postLastVisibleItem(lastVisibleItem: Int) {
//        viewModelScope.launch {
//            _postLastVisibleItem.value = lastVisibleItem
//        }
//    }
//
//    fun resetPagingData() {
//        viewModelScope.launch {
//            _pagingData.value = emptyList()
//        }
//    }


    fun getCurrentUserData() {
        try {
            viewModelScope.launch {
                getCurrentUserDataUseCase().collect { userData ->
                    if (userData != null) {
                        _currentUserData.value = userData.toModel()
                        CurrentUser.userData = userData.toModel()
                    }
                }
            }
        } catch (e: Exception) {
            throw NullPointerException("${e}")
        }
    }

    fun getAllPost() {
        viewModelScope.launch {
            getAllPostUseCase().collect {
                val postList = it.toPostListModel()
                _allPostData.value = postList
            }
        }
    }


}