package com.example.project_sns.ui.view.main.profile

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.data.response.KakaoMapResponse
import com.example.project_sns.domain.usecase.GetCurrentUserPostDataUseCase
import com.example.project_sns.domain.usecase.PostUploadUseCase
import com.example.project_sns.domain.usecase.ProfileEditUseCase
import com.example.project_sns.domain.usecase.SearchKakaoMapUseCase
import com.example.project_sns.ui.mapper.toEntity
import com.example.project_sns.ui.mapper.toKakaoListEntity
import com.example.project_sns.ui.mapper.toListModel
import com.example.project_sns.ui.mapper.toModel
import com.example.project_sns.ui.util.CheckEditProfile
import com.example.project_sns.ui.view.model.KakaoDocumentsModel
import com.example.project_sns.ui.view.model.KakaoMapModel
import com.example.project_sns.ui.view.model.PostDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val postUpLoadUseCase: PostUploadUseCase,
    private val getCurrentUserPostDataUseCase: GetCurrentUserPostDataUseCase,
    private val profileEditUseCase: ProfileEditUseCase,
    private val searchKakaoMapUseCase: SearchKakaoMapUseCase
) : ViewModel() {

    private val _postUpLoadResult = MutableStateFlow<Boolean?>(null)
    val postUpLoadResult: StateFlow<Boolean?> get() = _postUpLoadResult

    private val _postInformation = MutableStateFlow<List<PostDataModel>>(emptyList())
    val postInformation: StateFlow<List<PostDataModel>> get() = _postInformation

    private val _editEvent = Channel<CheckEditProfile?> { }
    val editEvent = _editEvent.receiveAsFlow()

    private val _mapList = MutableStateFlow<List<KakaoDocumentsModel>>(emptyList())
    val mapList: StateFlow<List<KakaoDocumentsModel>> get() = _mapList

    fun upLoadPost(postData: PostDataModel) {
        viewModelScope.launch {
            postUpLoadUseCase(postData.toEntity()).collect { result ->
                _postUpLoadResult.value = result
            }
        }
    }

    fun getCurrentUserPost(uid: String) {
        viewModelScope.launch {
            getCurrentUserPostDataUseCase(uid).collect {
                val postList = it.toListModel()
                _postInformation.value = postList
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
            val editData = profileEditUseCase(
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
}