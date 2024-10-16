package com.example.project_sns.ui.view.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.project_sns.databinding.FragmentChatRoomBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.model.MessageDataModel
import com.example.project_sns.ui.util.chatDateFormat
import com.example.project_sns.ui.view.main.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

@AndroidEntryPoint
class ChatRoomFragment : BaseFragment<FragmentChatRoomBinding>() {

    private val chatViewModel: ChatViewModel by viewModels()

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private var recipientUid: String? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChatRoomBinding {
        return FragmentChatRoomBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        val currentUser = CurrentUser.userData?.uid
        mainSharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
            if (userData != null && currentUser != null) {
                recipientUid = userData.uid

                binding.btnChatTextSend.setOnClickListener {
                    lifecycleScope.launch {
                        chatViewModel.checkChatRoom(currentUser, userData.uid)
                        collectFlow()
                    }
                }
            }
        }

        binding.btnChatPhotoSend.visibility = View.INVISIBLE
        binding.btnChatTextSend.visibility = View.VISIBLE

        binding.ivChatRoomBack.setOnClickListener {
            backButton()
        }
    }

    private fun collectFlow() {

        val currentUser = CurrentUser.userData?.uid ?: throw NullPointerException("User Data Null!")
        val newChatRoomId = UUID.randomUUID().toString()
        val message = binding.etChat.text.toString()
        val messageId = UUID.randomUUID().toString()
        val sendAt = chatDateFormat(LocalDateTime.now())
       
        lifecycleScope.launch {
            chatViewModel.checkChatRoomData.observe(viewLifecycleOwner) { chatRoomData ->
                Log.d("tag_chat_room", "$chatRoomData")
                if (chatRoomData != null) {
                    sendMessage(chatRoomData.chatRoomId,
                        MessageDataModel(chatRoomData.senderUid, chatRoomData.chatRoomId, messageId, message, sendAt)
                    )
                } else {
                    sendFirstMessage(newChatRoomId, currentUser, recipientUid.toString(),
                        MessageDataModel(currentUser, newChatRoomId, messageId, message, sendAt)
                    )

                }
            }
        }
    }

    private fun sendFirstMessage(chatRoomId: String, senderUid: String, recipientUid: String, messageData: MessageDataModel) {
        lifecycleScope.launch {
            chatViewModel.sendFirstMessage(chatRoomId, senderUid, recipientUid, messageData)
            collectFirstMessageResult()
        }
    }

    private fun sendMessage(chatRoomId: String, messageData: MessageDataModel) {
        lifecycleScope.launch {
            chatViewModel.sendMessage(chatRoomId, messageData)
            collectMessageResult()
        }
    }

    private fun collectFirstMessageResult() {
        lifecycleScope.launch {
            chatViewModel.sendFirstMessageResult.observe(viewLifecycleOwner) { result ->
                Log.d("tag_chat_first", "$result")
                if (result == true) {
                    Toast.makeText(requireContext(), "첫 메세지 보내기 성공", Toast.LENGTH_SHORT).show()
                } else if (result == false) {
                    Toast.makeText(requireContext(), "첫 메세지 보내기 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun collectMessageResult() {
        lifecycleScope.launch {
            chatViewModel.sendMessageResult.observe(viewLifecycleOwner) { result ->
                Log.d("tag_chat_result", "$result")
                if (result == true) {
                    Toast.makeText(requireContext(), "메세지 보내기 성공", Toast.LENGTH_SHORT).show()
                } else if (result == false) {
                    Toast.makeText(requireContext(), "메세지 보내기 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}