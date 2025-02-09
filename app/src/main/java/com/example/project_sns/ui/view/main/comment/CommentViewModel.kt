package com.example.project_sns.ui.view.main.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.CheckCommentChangeUseCase
import com.example.project_sns.domain.usecase.CheckReCommentChangeUseCase
import com.example.project_sns.domain.usecase.DeleteReCommentUseCase
import com.example.project_sns.domain.usecase.GetReCommentDataUseCase
import com.example.project_sns.domain.usecase.UploadReCommentUseCase
import com.example.project_sns.ui.mapper.toEntity
import com.example.project_sns.ui.mapper.toReCommentListModel
import com.example.project_sns.ui.model.ReCommentDataModel
import com.example.project_sns.ui.model.ReCommentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val getReCommentDataUseCase: GetReCommentDataUseCase,
    private val deleteReCommentUseCase: DeleteReCommentUseCase,
    private val checkCommentChangeUseCase: CheckCommentChangeUseCase,
    private val uploadReCommentUseCase: UploadReCommentUseCase,
    private val checkReCommentChangeUseCase: CheckReCommentChangeUseCase
): ViewModel() {

    private val _reCommentListData = MutableLiveData<List<ReCommentModel>>(emptyList())
    val reCommentListData: LiveData<List<ReCommentModel>> get() = _reCommentListData

    private val _commentChangeResult = MutableLiveData<Boolean>()
    val commentChangeResult: LiveData<Boolean> get() = _commentChangeResult

    private val _reCommentData = MutableLiveData<Boolean?>(null)
    val reCommentData: LiveData<Boolean?> get() = _reCommentData

    private val _reCommentChangeResult = MutableLiveData<Boolean>()
    val reCommentChangeResult: LiveData<Boolean> get() = _reCommentChangeResult

    val reCommentLastVisibleItem = MutableStateFlow(0)


    fun getReCommentChangeResult(commentId: String) {
        viewModelScope.launch {
            checkReCommentChangeUseCase(commentId).collect { result ->
                _reCommentChangeResult.value = result
            }
        }
    }

    fun getCommentChangeResult(postId: String) {
        viewModelScope.launch {
            checkCommentChangeUseCase(postId).collect { result ->
                _commentChangeResult.value = result
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


    fun clearReCommentListData() {
        viewModelScope.launch {
            _reCommentListData.value = emptyList()
        }
    }

    fun getReComment(commentId: String, lastVisibleItem: Flow<Int>) {
        viewModelScope.launch {
            getReCommentDataUseCase(commentId, lastVisibleItem).collect {
                val reCommentList = it.toReCommentListModel()
                _reCommentListData.value = reCommentList
            }
        }
    }

    fun deleteReComment(commentId: String, reCommentId: String) {
        viewModelScope.launch {
            deleteReCommentUseCase(commentId, reCommentId)
        }
    }


}