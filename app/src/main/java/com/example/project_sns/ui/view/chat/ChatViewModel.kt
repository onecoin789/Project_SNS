package com.example.project_sns.ui.view.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.entity.MessageDataEntity
import com.example.project_sns.domain.usecase.CheckChatRoomDataUseCase
import com.example.project_sns.domain.usecase.SendFirstMessageUseCase
import com.example.project_sns.domain.usecase.SendMessageUseCase
import com.example.project_sns.ui.model.ChatRoomDataModel
import com.example.project_sns.ui.model.MessageDataModel
import com.example.project_sns.ui.model.toEntity
import com.example.project_sns.ui.model.toModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val checkChatRoomDataUseCase: CheckChatRoomDataUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val sendFirstMessageUseCase: SendFirstMessageUseCase
): ViewModel() {

    private val _checkChatRoomData = MutableLiveData<ChatRoomDataModel?>()
    val checkChatRoomData: LiveData<ChatRoomDataModel?> get() = _checkChatRoomData

    private val _sendMessageResult = MutableLiveData<Boolean?>()
    val sendMessageResult: LiveData<Boolean?> get() = _sendMessageResult

    private val _sendFirstMessageResult = MutableLiveData<Boolean?>()
    val sendFirstMessageResult: LiveData<Boolean?> get() = _sendFirstMessageResult


    fun clearResult() {
        viewModelScope.launch {
            _sendFirstMessageResult.value = null
            _sendMessageResult.value = null
        }
    }

    fun checkChatRoom(senderUid: String, recipientUid: String) {
        viewModelScope.launch {
            checkChatRoomDataUseCase(senderUid, recipientUid).collect { result ->
                _checkChatRoomData.value = result?.toModel()
            }
        }
    }

    fun sendFirstMessage(chatRoomId: String, senderUid: String, recipientUid: String, messageData: MessageDataModel) {
        viewModelScope.launch {
            val messageDataEntity = messageData.toEntity()
            sendFirstMessageUseCase(chatRoomId, senderUid, recipientUid, messageDataEntity).collect { result ->
                _sendFirstMessageResult.value = result
            }
        }
    }

    fun sendMessage(chatRoomId: String, messageData: MessageDataModel) {
        viewModelScope.launch {
            val messageDataEntity = messageData.toEntity()
            sendMessageUseCase(chatRoomId, messageDataEntity).collect { result ->
                _sendMessageResult.value = result
            }
        }
    }
}