package com.example.project_sns.ui.view.chat

import android.util.Log
import android.util.Printer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.FirebaseMessagingService
import com.example.project_sns.domain.usecase.CheckChatRoomDataUseCase
import com.example.project_sns.domain.usecase.CheckChatRoomListUseCase
import com.example.project_sns.domain.usecase.CheckChatRoomSessionUseCase
import com.example.project_sns.domain.usecase.CheckMessageDataUseCase
import com.example.project_sns.domain.usecase.CheckReadMessageUseCase
import com.example.project_sns.domain.usecase.DeleteChatRoomUseCase
import com.example.project_sns.domain.usecase.GetChatMessageDataUseCase
import com.example.project_sns.domain.usecase.GetChatRoomDataUseCase
import com.example.project_sns.domain.usecase.GetChatRoomListUseCase
import com.example.project_sns.domain.usecase.GetUserSessionUseCase
import com.example.project_sns.domain.usecase.SendFirstMessageUseCase
import com.example.project_sns.domain.usecase.SendMessageUseCase
import com.example.project_sns.ui.mapper.toChatRoomListModel
import com.example.project_sns.ui.mapper.toMessageListModel
import com.example.project_sns.ui.model.ChatRoomDataModel
import com.example.project_sns.ui.model.ChatRoomModel
import com.example.project_sns.ui.model.MessageDataModel
import com.example.project_sns.ui.model.MessageModel
import com.example.project_sns.ui.model.UploadMessageDataModel
import com.example.project_sns.ui.model.toEntity
import com.example.project_sns.ui.model.toModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.apache.commons.lang3.mutable.Mutable
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val checkChatRoomDataUseCase: CheckChatRoomDataUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val sendFirstMessageUseCase: SendFirstMessageUseCase,
    private val getChatRoomDataUseCase: GetChatRoomDataUseCase,
    private val checkChatRoomListUseCase: CheckChatRoomListUseCase,
    private val getChatRoomListUseCase: GetChatRoomListUseCase,
    private val checkMessageDataUseCase: CheckMessageDataUseCase,
    private val getChatMessageDataUseCase: GetChatMessageDataUseCase,
    private val checkChatRoomSessionUseCase: CheckChatRoomSessionUseCase,
    private val checkReadMessageUseCase: CheckReadMessageUseCase,
    private val getUserSessionUseCase: GetUserSessionUseCase,
    private val deleteChatRoomUseCase: DeleteChatRoomUseCase
): ViewModel() {

    private val _checkChatRoomData = MutableLiveData<Boolean?>()
    val checkChatRoomData: LiveData<Boolean?> get() = _checkChatRoomData

    private val _sendMessageResult = MutableLiveData<Boolean?>()
    val sendMessageResult: LiveData<Boolean?> get() = _sendMessageResult

    private val _sendFirstMessageResult = MutableLiveData<Boolean?>()
    val sendFirstMessageResult: LiveData<Boolean?> get() = _sendFirstMessageResult

    private val _chatRoomData = MutableLiveData<ChatRoomDataModel?>()
    val chatRoomData: LiveData<ChatRoomDataModel?> get() = _chatRoomData

    private val _checkChatRoomList = MutableLiveData<Boolean>()
    val checkChatRoomList: LiveData<Boolean> get() = _checkChatRoomList

    private val _chatRoomList = MutableLiveData<List<ChatRoomModel>>()
    val chatRoomList: LiveData<List<ChatRoomModel>> get() = _chatRoomList

    private val _checkMessageDataResult = MutableLiveData<Boolean>()
    val checkMessageDataResult: LiveData<Boolean> get() = _checkMessageDataResult

    private val _messageList = MutableLiveData<List<MessageModel>>()
    val messageList: LiveData<List<MessageModel>> get() = _messageList

    private val _chatRoomSession = MutableLiveData<Boolean>()
    val chatRoomSession: LiveData<Boolean> get() = _chatRoomSession

    private val _userSession = MutableLiveData<Boolean>()
    val userSession: LiveData<Boolean> get() = _userSession

    private val _chatRoomRecipientSession = MutableLiveData<Boolean>()
    val chatRoomRecipientSession: LiveData<Boolean> get() = _chatRoomRecipientSession

    private val _deleteChatRoomResult = MutableLiveData<Boolean>()
    val deleteChatRoomResult: LiveData<Boolean> get() = _deleteChatRoomResult

    val messageLastVisibleItem = MutableStateFlow<Int>(0)


    fun clearMessageList() {
        _messageList.value = emptyList()
        messageLastVisibleItem.value = 0
    }


    // 채팅방 삭제
    fun deleteChatRoom(chatRoomId: String) {
        viewModelScope.launch {
            deleteChatRoomUseCase(chatRoomId).collect { result ->
                _deleteChatRoomResult.value = result
            }
        }
    }

    // 상대방이 접속했는지 확인용
    fun getChatRoomRecipientSession(recipientUid: String, chatRoomId: String) {
        viewModelScope.launch {
            getUserSessionUseCase(recipientUid, chatRoomId).collect { session ->
                _chatRoomRecipientSession.value = session
            }
        }
    }

    fun getUserSession(sessionValue: Boolean) {
        viewModelScope.launch {
            _userSession.value = sessionValue
        }
    }

    fun checkReadMessage(chatRoomId: String, userSession: Boolean, recipientUid: String) {
        viewModelScope.launch {
            checkReadMessageUseCase(chatRoomId, userSession, recipientUid).collect {
                Log.d("ChatViewModel", "$it")
            }
        }
    }

    fun checkChatRoomSession(chatRoomId: String) {
        viewModelScope.launch {
            checkChatRoomSessionUseCase(chatRoomId).collect { session ->
                _chatRoomSession.value = session
            }
        }
    }

    fun checkChatRoomList() {
        viewModelScope.launch {
            checkChatRoomListUseCase().collect { result ->
                _checkChatRoomList.value = result
            }
        }
    }

    fun getChatRoomList() {
        viewModelScope.launch {
            getChatRoomListUseCase().collect { roomData ->
                val chatRoomList = roomData.toChatRoomListModel()
                _chatRoomList.value = chatRoomList
            }
        }
    }

    fun checkMessageData(chatRoomId: String) {
        viewModelScope.launch {
            checkMessageDataUseCase(chatRoomId).collect { result ->
                _checkMessageDataResult.value = result
            }
        }
    }

    fun getMessageList(chatRoomId: String, lastVisibleItem: Flow<Int>) {
        viewModelScope.launch {
            getChatMessageDataUseCase(chatRoomId, lastVisibleItem).collect { messageData ->
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

    fun sendFirstMessage(chatRoomId: String, token: String, sendUser: String, accessToken: String, senderUid: String, recipientUid: String, messageData: UploadMessageDataModel) {
        viewModelScope.launch {
            val messageDataEntity = messageData.toEntity()
            sendFirstMessageUseCase(
                chatRoomId = chatRoomId,
                token = token,
                sendUser = sendUser,
                accessToken = accessToken,
                senderUid = senderUid,
                recipientUid = recipientUid,
                messageData = messageDataEntity).collect { result ->
                _sendFirstMessageResult.value = result
            }
        }
    }

    fun sendMessage(chatRoomId: String, token: String, sendUser: String, accessToken: String, recipientUid: String, messageData: UploadMessageDataModel) {
        viewModelScope.launch {
            val messageDataEntity = messageData.toEntity()
            sendMessageUseCase(
                chatRoomId = chatRoomId,
                token = token,
                sendUser = sendUser,
                accessToken = accessToken,
                recipientUid = recipientUid,
                messageData = messageDataEntity
            ).collect { result ->
                _sendMessageResult.value = result
            }
        }
    }
}