package com.example.project_sns.ui.view.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_sns.domain.usecase.GetChatRoomDataUseCase
import com.example.project_sns.ui.model.ChatRoomDataModel
import com.example.project_sns.ui.model.toModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatSharedViewModel @Inject constructor(
    private val getChatRoomDataUseCase: GetChatRoomDataUseCase
) : ViewModel() {

    private val _chatRoomData = MutableLiveData<ChatRoomDataModel?>()
    val chatRoomData: LiveData<ChatRoomDataModel?> get() = _chatRoomData

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
}