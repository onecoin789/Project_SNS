package com.example.project_sns.ui.view.chat.chatlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentChatListBinding
import com.example.project_sns.ui.BaseDialog
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.BaseSnackBar
import com.example.project_sns.ui.model.ChatRoomModel
import com.example.project_sns.ui.model.UserDataModel
import com.example.project_sns.ui.view.chat.ChatSharedViewModel
import com.example.project_sns.ui.view.chat.ChatViewModel
import com.example.project_sns.ui.view.main.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ChatListFragment : BaseFragment<FragmentChatListBinding>() {

    private val chatViewModel: ChatViewModel by viewModels()

    private val chatSharedViewModel: ChatSharedViewModel by activityViewModels()

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private lateinit var friendListAdapter: ChatListFriendListAdapter

    private lateinit var chatRoomListAdapter: ChatRoomListAdapter

    private val itemTouchSimpleCallback = ItemTouchHelperCallback()

    private val itemTouchHelper = ItemTouchHelper(itemTouchSimpleCallback)

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChatListBinding {
        return FragmentChatListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRv()
        initView()
        checkChatRoomList()
        collectCheckChatRoomList()


    }


    private fun checkChatRoomList() {
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.checkChatRoomList()
        }
    }

    private fun collectCheckChatRoomList() {
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.checkChatRoomList.observe(viewLifecycleOwner) { result ->
                Log.d("ChatListFragment", "$result")
                if (result == true) {
                    binding.tvChatListNone.visibility = View.GONE
                    binding.tvChatList.visibility = View.VISIBLE
                    binding.rvChatList.visibility = View.VISIBLE
                    getChatList()
                } else {
                    binding.tvChatListNone.visibility = View.VISIBLE
                    binding.tvChatList.visibility = View.GONE
                    binding.rvChatList.visibility = View.GONE
                }
            }
        }
    }

    private fun getChatList() {
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.getChatRoomList()
        }
    }


    private fun initView() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.friendList.collect { friendList ->
                if (friendList.isEmpty()) {
                    binding.rvChatListFriend.visibility = View.GONE
                } else {
                    binding.rvChatListFriend.visibility = View.VISIBLE
                    friendListAdapter.submitList(friendList)
                }
            }
        }
        lifecycleScope.launch {
            chatViewModel.chatRoomList.observe(viewLifecycleOwner) { chatList ->
                Log.d("chatList", "$chatList")
                chatRoomListAdapter.submitList(chatList)
            }
        }
        binding.ivChatListBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRv() {
        friendListAdapter = ChatListFriendListAdapter { data ->
            checkChatRoomExist(data)
        }
        with(binding.rvChatListFriend) {
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = friendListAdapter
        }


        itemTouchHelper.attachToRecyclerView(binding.rvChatList)

        chatRoomListAdapter =
            ChatRoomListAdapter(object : ChatRoomListAdapter.ChatRoomListClickListener {
                override fun onListClickListener(data: ChatRoomModel) {
                    val userData = data.userData
                    val chatRoomId = data.chatRoomData.chatRoomId

                    Log.d("ChatListFragment", "$userData, $chatRoomId")

                    if (userData != null) {
                        chatSharedViewModel.getChatRoomId(chatRoomId)
                        chatSharedViewModel.getChatRoomData(userData.uid)
                        chatSharedViewModel.checkChatRoomExist(userData.uid)
                        onClickFriendList(userData)
                    } else {
                        chatSharedViewModel.getChatRoomId(chatRoomId)
                        chatSharedViewModel.clearCheckData()
                        onClickFriendList(null)
                    }
                }

                override fun onListDeleteClickListener(data: ChatRoomModel) {
                    val chatRoomId = data.chatRoomData.chatRoomId
                    val dialog = BaseDialog("채팅방 나가기", "삭제시 복구가 불가능 합니다.")
                    dialog.setButtonClickListener(object : BaseDialog.DialogClickEvent {
                        override fun onClickConfirm() {
                            chatSharedViewModel.clearChatRoomId()
                            chatViewModel.deleteChatRoom(chatRoomId)
                        }
                    })
                    dialog.show(childFragmentManager, "chatListDialog")
                }
            })

        val linearLayoutManager = LinearLayoutManager(requireContext())
        with(binding.rvChatList) {
            layoutManager = linearLayoutManager
            adapter = chatRoomListAdapter
            setOnTouchListener { _, _ ->
                itemTouchSimpleCallback.removePreviousClamp(binding.rvChatList)
                false
            }
        }
    }

    private fun onClickFriendList(friendData: UserDataModel?) {
        Log.d("ChatListFragment", "$friendData")
        if (friendData != null) {
            mainSharedViewModel.getUserData(friendData.uid)
            findNavController().navigate(R.id.chatRoomFragment)
        } else {
            mainSharedViewModel.getUserData(null)
            findNavController().navigate(R.id.chatRoomFragment)

        }
    }

    private fun checkFirst(userData: UserDataModel) {
        chatSharedViewModel.checkChatRoomExist(userData.uid)
    }

    private fun getChatRoomData(userData: UserDataModel) {
        chatViewModel.getChatRoomData(userData.uid)
    }

    private fun checkChatRoomExist(userData: UserDataModel) {
        CoroutineScope(Dispatchers.Main).launch {
            Log.d("ChatListFragment_userData", "$userData")
            checkFirst(userData)
            delay(200)
            chatSharedViewModel.checkChatRoomData.observe(viewLifecycleOwner) { result ->
                Log.d("ChatListFragment", "$result")
                if (result == true) {
                    getChatRoomData(userData)
                    Log.d("ChatListFragment", "1")
                    chatViewModel.chatRoomData.observe(viewLifecycleOwner) { data ->
                        if (data != null) {
                            chatSharedViewModel.getChatRoomId(data.chatRoomId)
                            onClickFriendList(userData)
                        }
                    }
                } else if (result == false) {
                    onClickFriendList(userData)
                    Log.d("ChatListFragment", "2")
                }
            }
        }
    }


}