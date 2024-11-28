package com.example.project_sns.ui.view.chat

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentChatRoomBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.model.MessageDataModel
import com.example.project_sns.ui.model.toType
import com.example.project_sns.ui.util.chatDateFormat
import com.example.project_sns.ui.util.dateFormat
import com.example.project_sns.ui.view.main.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.TimeZone
import java.util.UUID

@AndroidEntryPoint
class ChatRoomFragment : BaseFragment<FragmentChatRoomBinding>() {

    private val chatViewModel: ChatViewModel by viewModels()

    private val chatSharedViewModel: ChatSharedViewModel by activityViewModels()

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private var chatImageList: MutableList<Uri>? = null

    private lateinit var messageListAdapter: MessageListAdapter

    private lateinit var chatRoomImageListAdapter: ChatRoomImageListAdapter

    private var messageListSize: Int = 0

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChatRoomBinding {
        return FragmentChatRoomBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRv()
        initView()
        checkChatRoomData()
        checkMessage()
        checkMessageDataResult()
        editTextWatcher()
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

        binding.btnChatPhotoSend.setOnClickListener {
            TedImagePicker.with(requireContext()).imageAndVideo()
                .buttonBackground(R.color.point)
                .buttonTextColor(R.color.text)
                .max(10, "이미지는 최대 10장 입니다.")
                .startMultiImage { uri ->

                    chatImageList = uri.toMutableList()
                    chatRoomImageListAdapter.submitList(chatImageList)

                    Log.d("image_count", "$chatImageList")

                    if (chatImageList == null) {
                        binding.clChatRoomImageList.visibility = View.GONE
                    } else {
                        binding.clChatRoomImageList.visibility = View.VISIBLE
                    }
                }
        }


        binding.tvChatRoomImageCancel.setOnClickListener {
            chatImageList = null
            binding.clChatRoomImageList.visibility = View.GONE
            Log.d("image_count", "$chatImageList")
        }


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

        chatRoomImageListAdapter =
            ChatRoomImageListAdapter(object : ChatRoomImageListAdapter.ChatRoomImageClickListener {
                override fun onClickImageCancel(item: Uri) {
                    CoroutineScope(Dispatchers.Main).launch {
                        chatImageList?.remove(item)
                        binding.rvChatRoomImage.visibility = View.GONE
                        delay(100)
                        if (chatImageList?.isEmpty() == true) {
                            chatImageList = null
                            binding.rvChatRoomImage.visibility = View.VISIBLE
                            binding.clChatRoomImageList.visibility = View.GONE
                        } else {
                            chatRoomImageListAdapter.submitList(chatImageList)
                            binding.rvChatRoomImage.visibility = View.VISIBLE
                        }
                        Log.d("chatImageList", "$chatImageList, $item")
                    }
                }

            })
        val imageLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        with(binding.rvChatRoomImage) {
            layoutManager = imageLayoutManager
            adapter = chatRoomImageListAdapter
        }
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
        val sendAt = chatDateFormat()
        Log.d("date", sendAt)
        // FIXME: localtime 말고 utc로 올리기 시로 변경

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

    private fun scroll() {
        binding.rvChat.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView.smoothScrollToPosition(messageListSize - 1)
            }
        })
    }

    private fun editTextWatcher() {
        binding.etChat.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (binding.etChat.text.isEmpty()) {
                    binding.btnChatTextSend.visibility = View.INVISIBLE
                    binding.btnChatPhotoSend.visibility = View.VISIBLE
                } else {
                    binding.btnChatTextSend.visibility = View.VISIBLE
                    binding.btnChatPhotoSend.visibility = View.INVISIBLE
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.etChat.text.isEmpty()) {
                    binding.btnChatTextSend.visibility = View.INVISIBLE
                    binding.btnChatPhotoSend.visibility = View.VISIBLE
                } else {
                    binding.btnChatTextSend.visibility = View.VISIBLE
                    binding.btnChatPhotoSend.visibility = View.INVISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.etChat.text.isEmpty()) {
                    binding.btnChatTextSend.visibility = View.INVISIBLE
                    binding.btnChatPhotoSend.visibility = View.VISIBLE
                } else {
                    binding.btnChatTextSend.visibility = View.VISIBLE
                    binding.btnChatPhotoSend.visibility = View.INVISIBLE
                }
            }

        })
    }

}