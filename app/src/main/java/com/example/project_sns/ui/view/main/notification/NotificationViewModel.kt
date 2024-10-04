package com.example.project_sns.ui.view.main.notification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.AcceptFriendRequestUseCase
import com.example.project_sns.domain.usecase.DeleteFriendUseCase
import com.example.project_sns.domain.usecase.GetRequestDataUseCase
import com.example.project_sns.domain.usecase.RejectFriendRequestUseCase
import com.example.project_sns.ui.mapper.toRequestDataModel
import com.example.project_sns.ui.view.model.RequestModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getRequestDataUseCase: GetRequestDataUseCase,
    private val acceptFriendUseCase: AcceptFriendRequestUseCase,
    private val rejectFriendRequestUseCase: RejectFriendRequestUseCase,
): ViewModel() {

    private val _requestList = MutableLiveData<List<RequestModel>>()
    val requestList: LiveData<List<RequestModel>> get() = _requestList

    private val _acceptResult = MutableStateFlow<Boolean?>(null)
    val acceptResult: StateFlow<Boolean?> get() = _acceptResult

    private val _rejectResult = MutableStateFlow<Boolean?>(null)
    val rejectResult: StateFlow<Boolean?> get() = _rejectResult



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

    fun getRequestList() {
        viewModelScope.launch {
            getRequestDataUseCase().collect { data ->
                _requestList.value = data.toRequestDataModel()
                Log.d("requestList", "${requestList.value}")
            }
        }
    }
}