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
import com.example.project_sns.ui.model.toType
import com.example.project_sns.ui.util.chatDateFormat
import com.example.project_sns.ui.util.dateFormat
import com.example.project_sns.ui.view.main.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

@AndroidEntryPoint
class ChatRoomFragment : BaseFragment<FragmentChatRoomBinding>() {

    private val chatViewModel: ChatViewModel by viewModels()

    private val chatSharedViewModel: ChatSharedViewModel by activityViewModels()

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private lateinit var messageListAdapter: MessageListAdapter

    private var messageListSize: Int = 0

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
        checkChatRoomData()
        checkMessage()
        checkMessageDataResult()
    }

    private fun checkMessage() {
        chatSharedViewModel.chatRoomId.observe(viewLifecycleOwner) { chatRoomId ->
            if (chatRoomId != null) {
                chatViewModel.checkMessageData(chatRoomId)
            }
        }
    }

    private fun checkChatRoomExist() {
        mainSharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
            if (userData != null) {
                chatSharedViewModel.checkChatRoomExist(userData.uid)
            }
        }
    }

    private fun checkChatRoomData() {
        chatSharedViewModel.checkChatRoomData.observe(viewLifecycleOwner) { result ->
            Log.d("checkData", "$result")
            if (result == true) {
                binding.tvChatRoomNone.visibility = View.GONE
                binding.rvChat.visibility = View.VISIBLE
                getMessageList()
            } else if (result == false) {
                binding.tvChatRoomNone.visibility = View.VISIBLE
                binding.rvChat.visibility = View.GONE
            }
        }
    }


    private fun getChatRoomData() {
        CoroutineScope(Dispatchers.Main).launch {
            mainSharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
                if (userData != null) {
                    chatViewModel.getChatRoomData(userData.uid)
                }
            }
            delay(500)
            chatViewModel.chatRoomData.observe(viewLifecycleOwner) {
                if (it != null)
                    chatSharedViewModel.getChatRoomId(it.chatRoomId)
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

        binding.btnChatTextSend.setOnClickListener {
            checkMessageState()
        }

        binding.btnChatPhotoSend.visibility = View.INVISIBLE
        binding.btnChatTextSend.visibility = View.VISIBLE

        binding.ivChatRoomBack.setOnClickListener {
            chatSharedViewModel.clearChatRoomId()
            chatSharedViewModel.clearCheckData()
            backButton()
        }
    }

    private fun initRv() {
        chatViewModel.messageList.observe(viewLifecycleOwner) { messageList ->
            Log.d("message", "$messageList")
            messageListAdapter.submitList(messageList)
            messageListSize = messageList.size
        }

        messageListAdapter =
            MessageListAdapter(object : MessageListAdapter.MessageItemClickListener {

            })

        val linearLayoutManager = LinearLayoutManager(requireContext())
        with(binding.rvChat) {
            layoutManager = linearLayoutManager
            adapter = messageListAdapter
        }
    }

    private fun getMessageList() {
        val lastVisibleItem = chatViewModel.messageLastVisibleItem
        viewLifecycleOwner.lifecycleScope.launch {
            chatSharedViewModel.chatRoomId.observe(viewLifecycleOwner) { chatRoomId ->
                Log.d("chatRoomId", "$chatRoomId")
                if (chatRoomId != null) {
                    chatViewModel.getMessageList(chatRoomId, lastVisibleItem)
                }
            }
        }
    }

    private fun checkMessageState() {
        val currentUser = CurrentUser.userData?.uid ?: throw NullPointerException("User Data Null!")
        val newChatRoomId = UUID.randomUUID().toString()
        val message = binding.etChat.text.toString()
        val messageId = UUID.randomUUID().toString()
        val sendAt = chatDateFormat(LocalDateTime.now())

        chatSharedViewModel.checkChatRoomData.observe(viewLifecycleOwner) { result ->
            if (result == true) {
                sendMessage(currentUser, message, messageId, sendAt)
            } else if (result == false) {
                sendFirstMessage(
                    newChatRoomId,
                    currentUser,
                    MessageDataModel(currentUser, newChatRoomId, messageId, message, sendAt)
                )
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
        chatSharedViewModel.chatRoomData.observe(viewLifecycleOwner) { chatRoom ->
            Log.d("chatRoomData", "$chatRoom")
            if (chatRoom != null) {
                val messageData =
                    MessageDataModel(senderUid, chatRoom.chatRoomId, messageId, message, sendAt)
                chatViewModel.sendMessage(chatRoom.chatRoomId, messageData)
                collectMessageResult()
            }
        }
    }

    private fun setChatRoom() {
        CoroutineScope(Dispatchers.Main).launch {
            checkChatRoomExist()
            delay(500)
            getChatRoomData()
        }
    }

    private fun collectFirstMessageResult() {
        lifecycleScope.launch {
            chatViewModel.sendFirstMessageResult.observe(viewLifecycleOwner) { result ->
                if (result == true) {
                    binding.etChat.text.clear()
                    setChatRoom()
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
                    getMessageList()
                    Log.d("messageSize", "$messageListSize")
                } else if (result == false) {
                    Toast.makeText(requireContext(), "메세지 보내기 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun checkMessageDataResult() {
        lifecycleScope.launch {
            chatViewModel.checkMessageDataResult.observe(viewLifecycleOwner) { result ->
                if (result == true) {
                    getMessageList()
                    Toast.makeText(requireContext(), "메세지 받기 성공", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "메세지 보내기 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}