package com.example.project_sns.ui.view.main.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.DeleteReCommentUseCase
import com.example.project_sns.domain.usecase.GetReCommentDataUseCase
import com.example.project_sns.ui.mapper.toReCommentListModel
import com.example.project_sns.ui.model.ReCommentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val getReCommentDataUseCase: GetReCommentDataUseCase,
    private val deleteReCommentUseCase: DeleteReCommentUseCase
): ViewModel() {

    private val _reCommentListData = MutableLiveData<List<ReCommentModel>>(emptyList())
    val reCommentListData: LiveData<List<ReCommentModel>> get() = _reCommentListData


    val reCommentLastVisibleItem = MutableStateFlow(0)



    fun clearReCommentData() {
        viewModelScope.launch {
            _reCommentListData.value = emptyList()
            reCommentLastVisibleItem.value = 0
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