package com.example.project_sns.ui.view.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.CheckChatRoomDataUseCase
import com.example.project_sns.domain.usecase.GetChatRoomDataUseCase
import com.example.project_sns.ui.model.ChatRoomDataModel
import com.example.project_sns.ui.model.toModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatSharedViewModel @Inject constructor(
    private val checkChatRoomDataUseCase: CheckChatRoomDataUseCase,
    private val getChatRoomDataUseCase: GetChatRoomDataUseCase
) : ViewModel() {

    private val _checkChatRoomData = MutableLiveData<Boolean?>()
    val checkChatRoomData: LiveData<Boolean?> get() = _checkChatRoomData

    private val _chatRoomData = MutableLiveData<ChatRoomDataModel?>()
    val chatRoomData: LiveData<ChatRoomDataModel?> get() = _chatRoomData

    private val _chatRoomId = MutableLiveData<String?>()
    val chatRoomId: LiveData<String?> get() = _chatRoomId


    fun getChatRoomId(id: String) {
        _chatRoomId.value = id
        Log.d("CSVM", "${_chatRoomId.value}")
    }

    fun clearChatRoomId() {
        _chatRoomId.value = null
    }

    fun clearCheckData() {
        viewModelScope.launch {
            _checkChatRoomData.value = null
        }
    }
    fun checkChatRoomExist(recipientUid: String) {
        viewModelScope.launch {
            checkChatRoomDataUseCase(recipientUid).collect { result ->
                _checkChatRoomData.value = result
            }
        }
    }

    fun getChatRoomData(recipientUid: String) {
        viewModelScope.launch {
            getChatRoomDataUseCase(recipientUid).collect { data ->
                if (data != null) {
                    val chatRoomModel = data.toModel()
                    _chatRoomData.value = chatRoomModel
                    Log.d("chatRoom", "$chatRoomModel")
                }
            }
        }
    }
}