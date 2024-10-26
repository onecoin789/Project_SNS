package com.example.project_sns.ui.view.main.notification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.AcceptFriendRequestUseCase
import com.example.project_sns.domain.usecase.CheckRequestListUseCase
import com.example.project_sns.domain.usecase.DeleteFriendUseCase
import com.example.project_sns.domain.usecase.GetRequestDataUseCase
import com.example.project_sns.domain.usecase.RejectFriendRequestUseCase
import com.example.project_sns.ui.mapper.toRequestDataModel
import com.example.project_sns.ui.model.RequestModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getRequestDataUseCase: GetRequestDataUseCase,
    private val acceptFriendUseCase: AcceptFriendRequestUseCase,
    private val rejectFriendRequestUseCase: RejectFriendRequestUseCase,
    private val checkRequestListUseCase: CheckRequestListUseCase
): ViewModel() {

    private val _requestList = MutableLiveData<List<RequestModel>>()
    val requestList: LiveData<List<RequestModel>> get() = _requestList

    private val _acceptResult = MutableStateFlow<Boolean?>(null)
    val acceptResult: StateFlow<Boolean?> get() = _acceptResult

    private val _rejectResult = MutableStateFlow<Boolean?>(null)
    val rejectResult: StateFlow<Boolean?> get() = _rejectResult

    private val _checkRequestResult = MutableLiveData<Boolean>()
    val checkRequestResult: LiveData<Boolean> get() = _checkRequestResult


    val requestLastVisibleItem = MutableStateFlow(0)

    fun rejectRequest(requestId: String) {
        viewModelScope.launch {
            rejectFriendRequestUseCase(requestId).collect { result ->
                _rejectResult.value = result
            }
        }
    }

    fun acceptFriend(requestId: String, fromUid: String, toUid: String) {
        viewModelScope.launch {
            acceptFriendUseCase(requestId, fromUid, toUid).collect { result ->
                _acceptResult.value = result
            }
        }
    }

    fun getRequestList(lastVisibleItem: Flow<Int>) {
        viewModelScope.launch {
            getRequestDataUseCase(lastVisibleItem).collect { data ->
                _requestList.value = data.toRequestDataModel()
            }
        }
    }

    fun clearRequestItem() {
        viewModelScope.launch {
            requestLastVisibleItem.value = 0
            _requestList.value = emptyList()
        }
    }

    fun checkRequestList() {
        viewModelScope.launch {
            checkRequestListUseCase().collect { result ->
                _checkRequestResult.value = result
                Log.d("check_request", "$result")
            }
        }
    }
}