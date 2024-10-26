package com.example.project_sns.ui.view.chat

import android.os.Bundle
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ChatListFragment : BaseFragment<FragmentChatListBinding>() {

    private val chatViewModel: ChatViewModel by viewModels()

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private lateinit var friendListAdapter: ChatListFriendListAdapter

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
        binding.ivChatListBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initRv() {
        friendListAdapter = ChatListFriendListAdapter { data ->
            onClickFriendList(data)
        }
        with(binding.rvChatListFriend) {
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = friendListAdapter
        }
        friendListAdapter.getFriendList()
    }

    private fun onClickFriendList(friendData: UserDataModel) {
        mainSharedViewModel.getUserData(friendData.uid)
        findNavController().navigate(R.id.action_chatListFragment_to_chatRoomFragment)
    }

}