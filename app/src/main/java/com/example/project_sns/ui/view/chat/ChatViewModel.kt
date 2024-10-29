package com.example.project_sns.ui.view.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.CheckChatRoomDataUseCase
import com.example.project_sns.domain.usecase.GetChatMessageDataUseCase
import com.example.project_sns.domain.usecase.GetChatRoomDataUseCase
import com.example.project_sns.domain.usecase.GetChatRoomListUseCase
import com.example.project_sns.domain.usecase.SendFirstMessageUseCase
import com.example.project_sns.domain.usecase.SendMessageUseCase
import com.example.project_sns.ui.mapper.toChatRoomListModel
import com.example.project_sns.ui.mapper.toMessageListModel
import com.example.project_sns.ui.model.ChatRoomDataModel
import com.example.project_sns.ui.model.ChatRoomModel
import com.example.project_sns.ui.model.MessageDataModel
import com.example.project_sns.ui.model.MessageModel
import com.example.project_sns.ui.model.toEntity
import com.example.project_sns.ui.model.toModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val checkChatRoomDataUseCase: CheckChatRoomDataUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val sendFirstMessageUseCase: SendFirstMessageUseCase,
    private val getChatRoomDataUseCase: GetChatRoomDataUseCase,
    private val getChatRoomListUseCase: GetChatRoomListUseCase,
    private val getChatMessageDataUseCase: GetChatMessageDataUseCase
): ViewModel() {

    private val _checkChatRoomData = MutableLiveData<Boolean?>()
    val checkChatRoomData: LiveData<Boolean?> get() = _checkChatRoomData

    private val _sendMessageResult = MutableLiveData<Boolean?>()
    val sendMessageResult: LiveData<Boolean?> get() = _sendMessageResult

    private val _sendFirstMessageResult = MutableLiveData<Boolean?>()
    val sendFirstMessageResult: LiveData<Boolean?> get() = _sendFirstMessageResult

    private val _chatRoomData = MutableLiveData<ChatRoomDataModel?>()
    val chatRoomData: LiveData<ChatRoomDataModel?> get() = _chatRoomData

    private val _chatRoomList = MutableLiveData<List<ChatRoomModel>>()
    val chatRoomList: LiveData<List<ChatRoomModel>> get() = _chatRoomList

    private val _messageList = MutableLiveData<List<MessageModel>>()
    val messageList: LiveData<List<MessageModel>> get() = _messageList


    fun getChatRoomList() {
        viewModelScope.launch {
            getChatRoomListUseCase().collect { roomData ->
                val chatRoomList = roomData.toChatRoomListModel()
                _chatRoomList.value = chatRoomList
            }
        }
    }

    fun getMessageList(chatRoomId: String) {
        viewModelScope.launch {
            getChatMessageDataUseCase(chatRoomId).collect { messageData ->
                val messageList = messageData.toMessageListModel()
                _messageList.value = messageList
            }
        }
    }

    fun getChatRoomData(recipientUid: String) {
        viewModelScope.launch {
            getChatRoomDataUseCase(recipientUid).collect { data ->
                if (data != null) {
                    val chatRoomModel = data.toModel()
                    _chatRoomData.value = chatRoomModel
                }
            }
        }
    }

    fun checkChatRoom(recipientUid: String) {
        viewModelScope.launch {
            checkChatRoomDataUseCase(recipientUid).collect { result ->
                _checkChatRoomData.value = result
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