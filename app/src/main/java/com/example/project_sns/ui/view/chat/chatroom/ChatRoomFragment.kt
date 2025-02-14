package com.example.project_sns.ui.view.chat.chatroom

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.project_sns.FcmUtil
import com.example.project_sns.R
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

    private var sendUser: String = ""

    private var token: String = ""

    private var recipientUid: String = ""

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
        readMessage()
        backButtonListener()


        sendUserSession()

        getMessageList()

        binding.rvChat.smoothScrollToPosition(messageListSize)
    }


    private fun initView() {

        sendUser = CurrentUser.userData?.name.toString()

        mainSharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
            if (userData != null) {
                binding.tvChatRoomName.text = userData.name
                binding.tvChatRoomEmail.text = userData.email

                recipientUid = userData.uid
                token = userData.token
                checkRecipientSession(recipientUid)
                checkTextMessageState(recipientUid)
                checkImageMessageState(recipientUid)
            }
        }


//        binding.btnChatTextSend.setOnClickListener {
//            checkTextMessageState()
//        }

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

//            binding.btnChatRoomSendPhoto.setOnClickListener {
//                checkImageMessageState()
//            }
        }


        binding.tvChatRoomImageCancel.setOnClickListener {
            chatImageList = null
            binding.clChatRoomImageList.visibility = View.GONE
            binding.clChatRoomSendChat.visibility = View.VISIBLE
            Log.d("image_count", "$chatImageList")
        }

        if (FcmUtil.clickState == true) {
            binding.ivChatRoomBack.setOnClickListener {
                FcmUtil.clickState = false
                chatBackToMain()
//                chatSharedViewModel.clearChatRoomId()
//                chatSharedViewModel.clearCheckData()
            }
        } else {
            binding.ivChatRoomBack.setOnClickListener {
                chatBackButton()
//                chatSharedViewModel.clearChatRoomId()
//                chatSharedViewModel.clearCheckData()
            }
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

    private fun sendUserSession() {
        chatViewModel.getUserSession(true)
    }


    private fun readMessage() {
        chatSharedViewModel.chatRoomId.observe(viewLifecycleOwner) { chatRoomId ->
            mainSharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
                if (chatRoomId != null) {
                    if (userData != null) {
                        chatViewModel.userSession.observe(viewLifecycleOwner) { userSession ->
                            chatViewModel.checkReadMessage(chatRoomId, userSession, userData.uid)
                        }
                    }
                }
            }
        }
    }

    private fun checkRecipientSession(recipientUid: String) {
        chatSharedViewModel.chatRoomId.observe(viewLifecycleOwner) { chatRoomId ->
            if (chatRoomId != null) {
                chatViewModel.getChatRoomRecipientSession(recipientUid, chatRoomId)
            }
        }
    }


    private fun checkMessage() {
        chatSharedViewModel.chatRoomId.observe(viewLifecycleOwner) { chatRoomId ->
            if (chatRoomId != null) {
                chatViewModel.checkMessageData(chatRoomId)
                chatViewModel.checkChatRoomSession(chatRoomId)
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
            when (result) {
                true -> {
                    binding.tvChatRoomNone.visibility = View.GONE
                    binding.rvChat.visibility = View.VISIBLE
                    getMessageList()
                }

                false -> {
                    binding.tvChatRoomNone.visibility = View.VISIBLE
                    binding.rvChat.visibility = View.GONE
                }

                null -> {
                    binding.tvChatRoomNone.visibility = View.GONE
                    binding.rvChat.visibility = View.VISIBLE
                    binding.etChat.visibility = View.GONE
                    binding.tvChatRoomEmail.visibility = View.GONE
                    binding.tvChatRoomName.text = "탈퇴한 유저입니다."
                    getMessageList()
                }
            }
        }
    }


    private fun getChatRoomData() {
        CoroutineScope(Dispatchers.Main).launch {
            mainSharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
                if (userData != null) {
                    chatSharedViewModel.getChatRoomData(userData.uid)
                }
            }
            delay(500)
            chatSharedViewModel.chatRoomData.observe(viewLifecycleOwner) {
                if (it != null) {
                    chatSharedViewModel.getChatRoomId(it.chatRoomId)
                    checkChatRoomExist()
                }
            }
            delay(500)
            getMessageList()
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
            binding.rvChat.smoothScrollToPosition(messageListSize)
        }
    }


    // text message send
    private fun checkTextMessageState(recipientUid: String) {
        val currentUser = CurrentUser.userData?.uid ?: throw NullPointerException("User Data Null!")
        val newChatRoomId = UUID.randomUUID().toString()
        val messageId = UUID.randomUUID().toString()
        val sendAt = chatDateFormat()
        val accessToken = FcmUtil.accessToken.toString()
        val readUser = arrayListOf(mapOf(currentUser to true), mapOf(recipientUid to false))

        chatSharedViewModel.checkChatRoomData.observe(viewLifecycleOwner) { result ->
            if (result == true) {
                chatViewModel.chatRoomRecipientSession.observe(viewLifecycleOwner) { session ->
                    if (session == true) {
                        sendTextMessageBySession(currentUser, accessToken, true)
                    } else {
                        sendTextMessageBySession(currentUser, accessToken, false)
                    }
                }
            } else if (result == false) {
                binding.btnChatTextSend.setOnClickListener {
                    val message = binding.etChat.text.toString()
                    sendFirstMessage(
                        newChatRoomId,
                        currentUser,
                        accessToken,
                        UploadMessageDataModel(
                            uid = currentUser,
                            chatRoomId = newChatRoomId,
                            messageId = messageId,
                            message = message,
                            imageList = null,
                            sendAt = sendAt,
                            read = readUser,
                            type = MessageViewType.TEXT_MESSAGE
                        )
                    )
                }
            }
        }
    }

    private fun sendTextMessageBySession(senderUid: String, accessToken: String, session: Boolean) {

        binding.btnChatTextSend.setOnClickListener {
            val message = binding.etChat.text.toString()
            val messageId = UUID.randomUUID().toString()
            val sendAt = chatDateFormat()

            sendMessage(senderUid, message, messageId, sendAt, accessToken, session)
        }
    }

    // image message send
    private fun checkImageMessageState(recipientUid: String) {
        val currentUser = CurrentUser.userData?.uid ?: throw NullPointerException("User Data Null!")
        val newChatRoomId = UUID.randomUUID().toString()
        val messageId = UUID.randomUUID().toString()
        val sendAt = chatDateFormat()
        val accessToken = FcmUtil.accessToken.toString()
        val readUser = arrayListOf(mapOf(currentUser to true), mapOf(recipientUid to false))


        chatSharedViewModel.checkChatRoomData.observe(viewLifecycleOwner) { result ->
            if (result == true) {
                chatViewModel.chatRoomRecipientSession.observe(viewLifecycleOwner) { session ->
                    if (session == true) {
                        sendImageMessageBySession(currentUser, accessToken, true)
                    } else {
                        sendImageMessageBySession(currentUser, accessToken, false)
                    }
                }
            } else if (result == false) {
                sendFirstImageTypeMessage(
                    newChatRoomId,
                    currentUser,
                    accessToken,
                    UploadMessageDataModel(
                        uid = currentUser,
                        chatRoomId = newChatRoomId,
                        messageId = messageId,
                        message = null,
                        imageList = chatImageList,
                        sendAt = sendAt,
                        read = readUser,
                        type = MessageViewType.TEXT_MESSAGE
                    )
                )
            }
        }
    }

    private fun sendImageMessageBySession(
        senderUid: String,
        accessToken: String,
        session: Boolean
    ) {

        binding.btnChatRoomSendPhoto.setOnClickListener {
            val messageId = UUID.randomUUID().toString()
            val sendAt = chatDateFormat()

            sendImageTypeMessage(senderUid, messageId, sendAt, accessToken, session)
        }
    }


    // text message flow result
    private fun sendFirstMessage(
        chatRoomId: String,
        senderUid: String,
        accessToken: String,
        messageData: UploadMessageDataModel
    ) {
        lifecycleScope.launch {
            mainSharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
                if (userData != null) {
                    chatViewModel.sendFirstMessage(
                        chatRoomId,
                        token,
                        sendUser,
                        accessToken,
                        senderUid,
                        userData.uid,
                        messageData
                    )
                    collectFirstMessageResult()
                }
            }
        }
    }

    private fun sendMessage(
        senderUid: String,
        message: String,
        messageId: String,
        sendAt: String,
        accessToken: String,
        session: Boolean
    ) {
        chatSharedViewModel.chatRoomData.observe(viewLifecycleOwner) { chatRoom ->
            Log.d("chatRoomData", "$chatRoom")
            if (chatRoom != null) {
                val readUser = arrayListOf(mapOf(senderUid to true), mapOf(recipientUid to session))
                val messageData =
                    UploadMessageDataModel(
                        uid = senderUid,
                        chatRoomId = chatRoom.chatRoomId,
                        messageId = messageId,
                        message = message,
                        imageList = null,
                        sendAt = sendAt,
                        read = readUser,
                        type = MessageViewType.TEXT_MESSAGE
                    )
                chatViewModel.sendMessage(
                    chatRoom.chatRoomId,
                    token,
                    sendUser,
                    accessToken,
                    recipientUid,
                    messageData
                )
                collectMessageResult()
            }
        }
    }


//    private fun collectChatRoomSession() {
//        lifecycleScope.launch {
//            chatViewModel.chatRoomSession.observe(viewLifecycleOwner) { session ->
//                if (session == true) {
//                    Toast.makeText(requireContext(), "채팅방 들어옴", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(requireContext(), "채팅방 나감", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

    private fun collectFirstMessageResult() {
        lifecycleScope.launch {
            chatViewModel.sendFirstMessageResult.observe(viewLifecycleOwner) { result ->
                if (result == true) {
                    binding.etChat.text.clear()
                    setChatRoom()
                } else if (result == false) {
                    Log.d(TAG, "첫 메세지 보내기 실패")
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
                    Log.d(TAG, "메세지 보내기 실패")
                }
            }
        }
    }

    // image message flow result
    private fun sendFirstImageTypeMessage(
        chatRoomId: String,
        senderUid: String,
        accessToken: String,
        messageData: UploadMessageDataModel
    ) {
        lifecycleScope.launch {
            mainSharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
                if (userData != null) {
                    chatViewModel.sendFirstMessage(
                        chatRoomId,
                        token,
                        sendUser,
                        accessToken,
                        senderUid,
                        userData.uid,
                        messageData
                    )
                    collectFirstImageMessageResult()
                }
            }
        }
    }

    private fun sendImageTypeMessage(
        senderUid: String,
        messageId: String,
        sendAt: String,
        accessToken: String,
        session: Boolean
    ) {
        chatSharedViewModel.chatRoomData.observe(viewLifecycleOwner) { chatRoom ->
            if (chatRoom != null) {
                if (chatImageList != null) {
                    val readUser =
                        arrayListOf(mapOf(senderUid to true), mapOf(recipientUid to session))
                    val messageData =
                        UploadMessageDataModel(
                            uid = senderUid,
                            chatRoomId = chatRoom.chatRoomId,
                            messageId = messageId,
                            message = null,
                            imageList = chatImageList,
                            sendAt = sendAt,
                            read = readUser,
                            type = MessageViewType.IMAGE_MESSAGE
                        )
                    chatViewModel.sendMessage(
                        chatRoom.chatRoomId,
                        token,
                        sendUser,
                        accessToken,
                        recipientUid,
                        messageData
                    )
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
                    Log.d(TAG, "메세지 보내기 실패")
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
                    Log.d(TAG, "메세지 보내기 실패")
                }
            }
        }
    }


    private fun checkMessageDataResult() {
        lifecycleScope.launch {
            chatViewModel.checkMessageDataResult.observe(viewLifecycleOwner) { result ->
                if (result == true) {
                    getMessageList()
                } else {
                    Log.d(TAG, "메세지 받기 실패")
                }
            }
        }
    }

    private fun setChatRoom() {
        CoroutineScope(Dispatchers.Main).launch {
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

    private fun backButtonListener() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (FcmUtil.clickState == true) {
                    chatSharedViewModel.clearChatRoomId()
                    chatSharedViewModel.clearCheckData()
                    FcmUtil.clickState = false
                    chatBackToMain()
                } else {
                    chatSharedViewModel.clearChatRoomId()
                    chatSharedViewModel.clearCheckData()
                    chatBackButton()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this.viewLifecycleOwner,
            onBackPressedCallback = callback
        )
    }

    fun chatBackButton() {
        chatViewModel.getUserSession(false)
        findNavController().popBackStack()
    }

    fun chatBackToMain() {
        chatViewModel.getUserSession(false)
        findNavController().popBackStack(R.id.mainFragment, false)
    }

}