package com.example.project_sns.ui.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.GetAllPostUseCase
import com.example.project_sns.domain.usecase.GetCurrentUserDataUseCase
import com.example.project_sns.domain.usecase.GetPagingPostUseCase
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.mapper.toModel
import com.example.project_sns.ui.mapper.toPostDataListModel
import com.example.project_sns.ui.mapper.toPostListModel
import com.example.project_sns.ui.view.model.PostDataModel
import com.example.project_sns.ui.view.model.PostModel
import com.example.project_sns.ui.view.model.UserDataModel
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
    private val getPagingPostUseCase: GetPagingPostUseCase
) : ViewModel() {

    private val _currentUserData = MutableStateFlow<UserDataModel?>(null)
    val currentUserData: StateFlow<UserDataModel?> get() = _currentUserData

    private val _allPostData = MutableStateFlow<List<PostDataModel>>(emptyList())
    val allPostData: StateFlow<List<PostDataModel>> get() = _allPostData

    private val _pagingData = MutableLiveData<List<PostModel>>(emptyList())
    val pagingData: LiveData<List<PostModel>> get() = _pagingData

    private val _postLastVisibleItem = MutableStateFlow<Int>(0)
    val postLastVisibleItem: StateFlow<Int> get() = _postLastVisibleItem



//    val postLastVisibleItem = MutableStateFlow(0)

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


    fun getPagingData() {
        viewModelScope.launch {
            getPagingPostUseCase(postLastVisibleItem).collect { data ->
                _pagingData.value = data?.toPostDataListModel()
            }
        }
    }


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