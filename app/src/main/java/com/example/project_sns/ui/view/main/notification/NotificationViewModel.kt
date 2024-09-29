package com.example.project_sns.ui.view.main.notification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.GetRequestDataUseCase
import com.example.project_sns.ui.mapper.toRequestDataModel
import com.example.project_sns.ui.view.model.RequestModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getRequestDataUseCase: GetRequestDataUseCase
): ViewModel() {

    private val _requestList = MutableLiveData<List<RequestModel>>()
    val requestList: LiveData<List<RequestModel>> get() = _requestList


    fun getRequestList() {
        viewModelScope.launch {
            getRequestDataUseCase().collect { data ->
                _requestList.value = data.toRequestDataModel()
                Log.d("requestList", "${requestList.value}")
            }
        }
    }
}