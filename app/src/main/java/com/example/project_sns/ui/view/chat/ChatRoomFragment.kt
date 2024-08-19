package com.example.project_sns.ui.view.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.project_sns.databinding.FragmentChatRoomBinding
import com.example.project_sns.ui.BaseFragment

class ChatRoomFragment : BaseFragment<FragmentChatRoomBinding>() {


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChatRoomBinding {
        return FragmentChatRoomBinding.inflate(inflater, container, false)
    }

}