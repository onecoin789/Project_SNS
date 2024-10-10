package com.example.project_sns.ui.view.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentMyProfileFriendBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.main.MainViewModel
import com.example.project_sns.ui.view.model.UserDataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyProfileFriendFragment : BaseFragment<FragmentMyProfileFriendBinding>() {

    private val mainViewModel: MainViewModel by viewModels()

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMyProfileFriendBinding {
        return FragmentMyProfileFriendBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initRv()
        getFriendList()
    }

    private fun getFriendList() {
        val currentUser = CurrentUser.userData?.uid
        viewLifecycleOwner.lifecycleScope.launch {
            if (currentUser != null) {
                mainSharedViewModel.getFriendList(currentUser)
            }
        }
    }


    private fun initView() {
        binding.ivFriendBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initRv() {
        val currentUser = CurrentUser.userData?.uid
        val friendListAdapter = MyProfileFriendAdapter(object : MyProfileFriendAdapter.MyProfileFriendItemClickListener {
            override fun onClickFriendProfile(item: UserDataModel) {
                navigateFriendDetail(item)
            }

            override fun onClickFriendName(item: UserDataModel) {
                navigateFriendDetail(item)
            }

            override fun onClickFriendDelete(item: UserDataModel) {
                if (currentUser != null) {
                    mainSharedViewModel.deleteFriend(currentUser, item.uid)
                    getFriendList()
                }
            }
        })

        with(binding.rvFriend) {
            adapter = friendListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.friendList.collect { friendData ->
                friendListAdapter.submitList(friendData)
            }
        }
    }

    private fun navigateFriendDetail(item: UserDataModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.getUserData(item.uid)
            mainSharedViewModel.getUserPost(item.uid)
            mainSharedViewModel.checkFriendRequest(item.uid)
        }
        findNavController().navigate(R.id.friendDetailFragment)
    }

}