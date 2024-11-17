package com.example.project_sns.ui.view.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentChatListBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.model.UserDataModel
import com.example.project_sns.ui.view.main.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ChatListFragment : BaseFragment<FragmentChatListBinding>() {

    private val chatViewModel: ChatViewModel by viewModels()

    private val chatSharedViewModel: ChatSharedViewModel by activityViewModels()

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private lateinit var friendListAdapter: ChatListFriendListAdapter

    private lateinit var chatRoomListAdapter: ChatRoomListAdapter

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
        getChatList()

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

    private fun initRv() {
        friendListAdapter = ChatListFriendListAdapter { data ->
            checkChatRoomExist(data)
        }
        with(binding.rvChatListFriend) {
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = friendListAdapter
        }

        chatRoomListAdapter = ChatRoomListAdapter { data ->
            val userData = data.userData
            val chatRoomId = data.chatRoomData.chatRoomId

            chatSharedViewModel.getChatRoomId(chatRoomId)
            chatSharedViewModel.getChatRoomData(userData.uid)
            chatSharedViewModel.checkChatRoomExist(userData.uid)
            onClickFriendList(userData)

        }
        val linearLayoutManager = LinearLayoutManager(requireContext())
        with(binding.rvChatList) {
            layoutManager = linearLayoutManager
            adapter = chatRoomListAdapter
        }

    }

    private fun onClickFriendList(friendData: UserDataModel) {
        mainSharedViewModel.getUserData(friendData.uid)
        findNavController().navigate(R.id.chatRoomFragment)
    }

    private fun checkFirst(userData: UserDataModel) {
        chatSharedViewModel.checkChatRoomExist(userData.uid)
    }

    private fun getChatRoomData(userData: UserDataModel) {
        chatViewModel.getChatRoomData(userData.uid)
    }

    private fun checkChatRoomExist(userData: UserDataModel) {
        CoroutineScope(Dispatchers.Main).launch {
            checkFirst(userData)
            delay(200)
            chatSharedViewModel.checkChatRoomData.observe(viewLifecycleOwner) { result ->
                if (result == true) {
                    getChatRoomData(userData)
                    chatViewModel.chatRoomData.observe(viewLifecycleOwner) { data ->
                        if (data != null) {
                            chatSharedViewModel.getChatRoomId(data.chatRoomId)
                            onClickFriendList(userData)
                        }
                    }
                } else if (result == false) {
                    onClickFriendList(userData)
                }
            }
        }
    }



}