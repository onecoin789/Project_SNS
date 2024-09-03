package com.example.project_sns.ui.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.GetAllPostUseCase
import com.example.project_sns.domain.usecase.GetCurrentUserDataUseCase
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.CurrentUserModel
import com.example.project_sns.ui.mapper.toModel
import com.example.project_sns.ui.mapper.toPostListModel
import com.example.project_sns.ui.view.model.PostDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentUserDataUseCase: GetCurrentUserDataUseCase,
    private val getAllPostUseCase: GetAllPostUseCase
): ViewModel() {

    private val _currentUserData = MutableStateFlow<CurrentUserModel?>(null)
    val currentUserData: StateFlow<CurrentUserModel?> get() = _currentUserData

    private val _allPostData = MutableStateFlow<List<PostDataModel>>(emptyList())
    val allPostData : StateFlow<List<PostDataModel>> get() = _allPostData



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