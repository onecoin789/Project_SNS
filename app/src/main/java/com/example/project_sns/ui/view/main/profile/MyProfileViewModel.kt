package com.example.project_sns.ui.view.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.GetCurrentUserPostDataUseCase
import com.example.project_sns.domain.usecase.PostUploadUseCase
import com.example.project_sns.ui.mapper.toEntity
import com.example.project_sns.ui.mapper.toListModel
import com.example.project_sns.ui.view.model.PostDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val postUpLoadUseCase: PostUploadUseCase,
    private val getCurrentUserPostDataUseCase: GetCurrentUserPostDataUseCase
) : ViewModel() {

    private val _postUpLoadResult = MutableStateFlow<Boolean?>(null)
    val postUpLoadResult: StateFlow<Boolean?> get() = _postUpLoadResult

    private val _postInformation = MutableStateFlow<List<PostDataModel>>(emptyList())
    val postInformation: StateFlow<List<PostDataModel>> get() = _postInformation

    fun upLoadPost(postData: PostDataModel) {
        viewModelScope.launch {
            postUpLoadUseCase(postData.toEntity()).collect { result ->
                _postUpLoadResult.value = result
            }
        }
    }

    fun getPost(uid: String) {
        viewModelScope.launch {
            getCurrentUserPostDataUseCase(uid).collect {
                val postList = it.toListModel()
                _postInformation.value = postList
            }
        }
    }

}