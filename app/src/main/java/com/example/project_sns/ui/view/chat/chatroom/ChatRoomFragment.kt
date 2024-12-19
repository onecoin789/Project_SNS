package com.example.project_sns.ui.view.chat.chatroom

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
import com.example.project_sns.FcmUtil
import com.example.project_sns.FirebaseMessagingService
import com.example.project_sns.databinding.FragmentChatRoomBinding
import com.example.project_sns.domain.MessageViewType
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.model.MessageModel
import com.example.project_sns.ui.model.UploadMessageDataModel
import com.example.project_sns.ui.util.chatDateFormat
import com.example.project_sns.ui.view.chat.ChatSharedViewModel
import com.example.project_sns.ui.view.chat.ChatViewModel
import com.example.project_sns.ui.view.main.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@AndroidEntryPoint
class ChatRoomFragment : BaseFragment<FragmentChatRoomBinding>() {

    companion object {
        private const val TAG: String = "ChatRoomFragment"
    }

    private val chatViewModel: ChatViewModel by viewModels()

    private val chatSharedViewModel: ChatSharedViewModel by activityViewModels()

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private var chatImageList: MutableList<Uri>? = null

    private lateinit var messageListAdapter: MessageListAdapter

    private lateinit var chatRoomImageListAdapter: ChatRoomImageListAdapter

    private var recipientUser: String = ""

    private var token: String = ""


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

        Log.d(TAG, "${FcmUtil.accessToken}")
    }


    private fun initView() {

        recipientUser = CurrentUser.userData?.name.toString()

        mainSharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
            if (userData != null) {
                binding.tvChatRoomName.text = userData.name
                binding.tvChatRoomEmail.text = userData.email

                token = userData.token
            }
        }

        binding.btnChatTextSend.setOnClickListener {
            checkTextMessageState()
        }

        binding.btnChatPickPhoto.setOnClickListener {
            TedImagePicker.with(requireContext()).imageAndVideo()
                .max(10, "이미지는 최대 10장 입니다.")
                .startMultiImage { uri ->

                    chatImageList = uri.toMutableList()
                    chatRoomImageListAdapter.submitList(chatImageList)

                    Log.d("image_count", "$chatImageList")

                    if (chatImageList == null) {
                        binding.clChatRoomImageList.visibility = View.GONE
                        binding.clChatRoomSendChat.visibility = View.VISIBLE
                    } else {
                        binding.clChatRoomImageList.visibility = View.VISIBLE
                        binding.clChatRoomSendChat.visibility = View.GONE
                    }
                }

            binding.btnChatRoomSendPhoto.setOnClickListener {
                checkImageMessageState()
            }
        }


        binding.tvChatRoomImageCancel.setOnClickListener {
            chatImageList = null
            binding.clChatRoomImageList.visibility = View.GONE
            binding.clChatRoomSendChat.visibility = View.VISIBLE
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
                override fun onChatImageClickEvent(item: MessageModel) {
                    val image = item.messageData.imageList
                    if (image != null) {
                        val dialog = ChatRoomImageViewerDialog(image)
                        dialog.show(childFragmentManager, "imageViewer")
                    }
                }

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
                        binding.pbChatRoomImage.visibility = View.VISIBLE
                        delay(300)
                        if (chatImageList?.isEmpty() == true) {

                            chatImageList = null
                            binding.pbChatRoomImage.visibility = View.GONE
                            binding.rvChatRoomImage.visibility = View.VISIBLE
                            binding.clChatRoomSendChat.visibility = View.VISIBLE
                            binding.clChatRoomImageList.visibility = View.GONE
                        } else {
                            chatRoomImageListAdapter.submitList(chatImageList)
                            binding.pbChatRoomImage.visibility = View.GONE
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


    // text message send
    private fun checkTextMessageState() {
        val currentUser = CurrentUser.userData?.uid ?: throw NullPointerException("User Data Null!")
        val newChatRoomId = UUID.randomUUID().toString()
        val message = binding.etChat.text.toString()
        val messageId = UUID.randomUUID().toString()
        val sendAt = chatDateFormat()
        val accessToken = FcmUtil.accessToken.toString()

        chatSharedViewModel.checkChatRoomData.observe(viewLifecycleOwner) { result ->
            if (result == true) {
                sendMessage(currentUser, message, messageId, sendAt, accessToken)
            } else if (result == false) {
                sendFirstMessage(
                    newChatRoomId,
                    currentUser,
                    UploadMessageDataModel(
                        uid = currentUser,
                        chatRoomId = newChatRoomId,
                        messageId = messageId,
                        message = message,
                        imageList = null,
                        sendAt = sendAt,
                        type = MessageViewType.TEXT_MESSAGE)
                )
            }
        }
    }

    // image message send
    private fun checkImageMessageState() {
        val currentUser = CurrentUser.userData?.uid ?: throw NullPointerException("User Data Null!")
        val newChatRoomId = UUID.randomUUID().toString()
        val messageId = UUID.randomUUID().toString()
        val sendAt = chatDateFormat()
        val accessToken = FcmUtil.accessToken.toString()


        chatSharedViewModel.checkChatRoomData.observe(viewLifecycleOwner) { result ->
            if (result == true) {
                sendImageTypeMessage(currentUser, messageId, sendAt, accessToken)
            } else if (result == false) {
                sendFirstImageTypeMessage(
                    newChatRoomId,
                    currentUser,
                    UploadMessageDataModel(
                        uid = currentUser,
                        chatRoomId = newChatRoomId,
                        messageId = messageId,
                        message = null,
                        imageList = chatImageList,
                        sendAt = sendAt,
                        type = MessageViewType.TEXT_MESSAGE)
                )
            }
        }
    }


    // text message flow result
    private fun sendFirstMessage(
        chatRoomId: String,
        senderUid: String,
        messageData: UploadMessageDataModel
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

    private fun sendMessage(senderUid: String, message: String, messageId: String, sendAt: String, accessToken: String,) {
        chatSharedViewModel.chatRoomData.observe(viewLifecycleOwner) { chatRoom ->
            Log.d("chatRoomData", "$chatRoom")
            if (chatRoom != null) {
                val messageData =
                    UploadMessageDataModel(
                        uid = senderUid,
                        chatRoomId = chatRoom.chatRoomId,
                        messageId = messageId,
                        message = message,
                        imageList = null,
                        sendAt = sendAt,
                        type = MessageViewType.TEXT_MESSAGE
                    )
                chatViewModel.sendMessage(chatRoom.chatRoomId, token, recipientUser, accessToken, messageData)
                collectMessageResult()
            }
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

    // image message flow result
    private fun sendFirstImageTypeMessage(
        chatRoomId: String,
        senderUid: String,
        messageData: UploadMessageDataModel
    ) {
        lifecycleScope.launch {
            mainSharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
                if (userData != null) {
                    chatViewModel.sendFirstMessage(chatRoomId, senderUid, userData.uid, messageData)
                    collectFirstImageMessageResult()
                }
            }
        }
    }

    private fun sendImageTypeMessage(senderUid: String, messageId: String, sendAt: String, accessToken: String,) {
        chatSharedViewModel.chatRoomData.observe(viewLifecycleOwner) { chatRoom ->
            if (chatRoom != null) {
                if (chatImageList != null) {
                    val messageData =
                        UploadMessageDataModel(
                            uid = senderUid,
                            chatRoomId = chatRoom.chatRoomId,
                            messageId = messageId,
                            message = null,
                            imageList = chatImageList,
                            sendAt = sendAt,
                            type = MessageViewType.IMAGE_MESSAGE
                        )
                    chatViewModel.sendMessage(chatRoom.chatRoomId, token, recipientUser, accessToken, messageData)
                    collectImageMessageResult()
                }
            }
        }
    }

    private fun collectFirstImageMessageResult() {
        lifecycleScope.launch {
            chatViewModel.sendFirstMessageResult.observe(viewLifecycleOwner) { result ->
                if (result == true) {
                    binding.clChatRoomImageList.visibility = View.GONE
                    binding.clChatRoomSendChat.visibility = View.VISIBLE
                    setChatRoom()
                } else if (result == false) {
                    Toast.makeText(requireContext(), "메세지 보내기 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun collectImageMessageResult() {
        lifecycleScope.launch {
            chatViewModel.sendMessageResult.observe(viewLifecycleOwner) { result ->
                Log.d("chat_result", "$result")
                if (result == true) {
                    binding.clChatRoomImageList.visibility = View.GONE
                    binding.clChatRoomSendChat.visibility = View.VISIBLE
                    getMessageList()
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

    private fun setChatRoom() {
        CoroutineScope(Dispatchers.Main).launch {
            checkChatRoomExist()
            delay(500)
            getChatRoomData()
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
                    binding.btnChatPickPhoto.visibility = View.VISIBLE
                } else {
                    binding.btnChatTextSend.visibility = View.VISIBLE
                    binding.btnChatPickPhoto.visibility = View.INVISIBLE
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.etChat.text.isEmpty()) {
                    binding.btnChatTextSend.visibility = View.INVISIBLE
                    binding.btnChatPickPhoto.visibility = View.VISIBLE
                } else {
                    binding.btnChatTextSend.visibility = View.VISIBLE
                    binding.btnChatPickPhoto.visibility = View.INVISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.etChat.text.isEmpty()) {
                    binding.btnChatTextSend.visibility = View.INVISIBLE
                    binding.btnChatPickPhoto.visibility = View.VISIBLE
                } else {
                    binding.btnChatTextSend.visibility = View.VISIBLE
                    binding.btnChatPickPhoto.visibility = View.INVISIBLE
                }
            }

        })
    }

}