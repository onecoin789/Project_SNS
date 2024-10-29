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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_sns.databinding.FragmentChatRoomBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.model.MessageDataModel
import com.example.project_sns.ui.util.chatDateFormat
import com.example.project_sns.ui.util.dateFormat
import com.example.project_sns.ui.view.main.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

@AndroidEntryPoint
class ChatRoomFragment : BaseFragment<FragmentChatRoomBinding>() {

    private val chatViewModel: ChatViewModel by viewModels()

    private val chatSharedViewModel: ChatSharedViewModel by activityViewModels()

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private lateinit var messageListAdapter: MessageListAdapter

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChatRoomBinding {
        return FragmentChatRoomBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initRv()
        checkFirst()
        getMessageList()

    }


    private fun getMessageList() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.chatRoomId.observe(viewLifecycleOwner) { chatRoomId ->
                if (chatRoomId != null) {
                    chatViewModel.getMessageList(chatRoomId)
                }
            }
        }
    }


    private fun initView() {

        mainSharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
            if (userData != null) {
                binding.tvChatRoomName.text = userData.name
                binding.tvChatRoomEmail.text = userData.email
            }
        }

        chatViewModel.messageList.observe(viewLifecycleOwner) { messageList ->
            Log.d("message", "$messageList")
            messageListAdapter.submitList(messageList)
        }

        binding.btnChatTextSend.setOnClickListener {
            checkChatRoom()
        }

        binding.btnChatPhotoSend.visibility = View.INVISIBLE
        binding.btnChatTextSend.visibility = View.VISIBLE

        binding.ivChatRoomBack.setOnClickListener {
            mainSharedViewModel.clearChatRoomId()
            backButton()
        }
    }

    private fun initRv() {
        messageListAdapter = MessageListAdapter(object : MessageListAdapter.MessageItemClickListener {

        })

        val linearLayoutManager = LinearLayoutManager(requireContext())
        with(binding.rvChat) {
            layoutManager = linearLayoutManager
            adapter = messageListAdapter
        }
    }

    private fun checkFirst() {
        mainSharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
            if (userData != null) {
                chatViewModel.checkChatRoom(userData.uid)
            }
        }
    }

    private fun getChatRoomData() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
                if (userData != null) {
                    chatViewModel.getChatRoomData(userData.uid)
                }
            }
        }
    }

    private fun checkChatRoom() {

        val currentUser = CurrentUser.userData?.uid ?: throw NullPointerException("User Data Null!")
        val newChatRoomId = UUID.randomUUID().toString()
        val message = binding.etChat.text.toString()
        val messageId = UUID.randomUUID().toString()
        val sendAt = chatDateFormat(LocalDateTime.now())

        chatViewModel.checkChatRoomData.observe(viewLifecycleOwner) { result ->
            Log.d("check_result", "$result")
            if (result == true) {
                sendMessage(currentUser, message, messageId, sendAt)
            } else {
                sendFirstMessage(
                    newChatRoomId,
                    currentUser,
                    MessageDataModel(currentUser, newChatRoomId, messageId, message, sendAt)
                )
                checkFirst()
                getChatRoomData()
            }
        }
    }


    private fun sendFirstMessage(
        chatRoomId: String,
        senderUid: String,
        messageData: MessageDataModel
    ) {
        lifecycleScope.launch {
            mainSharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
                if (userData != null) {
                    chatViewModel.sendFirstMessage(chatRoomId, senderUid, userData.uid, messageData)
                    collectFirstMessageResult()
                }
            }
        }
    }

    private fun sendMessage(senderUid: String, message: String, messageId: String, sendAt: String) {
        lifecycleScope.launch {
            chatSharedViewModel.chatRoomData.observe(viewLifecycleOwner) { chatRoom ->
                Log.d("chatRoomData", "$chatRoom")
                if (chatRoom != null) {
                    val messageData = MessageDataModel(senderUid, chatRoom.chatRoomId, messageId, message, sendAt)
                    chatViewModel.sendMessage(chatRoom.chatRoomId, messageData)
                    collectMessageResult()
                    // FIXME: 요청 계속 보냄
                }
            }
        }
    }

    private fun collectFirstMessageResult() {
        lifecycleScope.launch {
            chatViewModel.sendFirstMessageResult.observe(viewLifecycleOwner) { result ->
                if (result == true) {
                    binding.etChat.text.clear()
                    getChatRoomData()
                } else if (result == false) {
                    Toast.makeText(requireContext(), "첫 메세지 보내기 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun collectMessageResult() {
        lifecycleScope.launch {
            chatViewModel.sendMessageResult.observe(viewLifecycleOwner) { result ->
                Log.d("chat_result", "$result")
                if (result == true) {
                    binding.etChat.text.clear()
                    getChatRoomData()
                } else if (result == false) {
                    Toast.makeText(requireContext(), "메세지 보내기 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}