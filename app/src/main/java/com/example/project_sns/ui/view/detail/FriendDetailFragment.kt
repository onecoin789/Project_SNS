package com.example.project_sns.ui.view.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentFriendDetailBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.main.MainViewModel
import com.example.project_sns.ui.view.main.notification.NotificationViewModel
import com.example.project_sns.ui.view.model.PostDataModel
import com.example.project_sns.ui.view.model.UserDataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.UUID


@AndroidEntryPoint
class FriendDetailFragment : BaseFragment<FragmentFriendDetailBinding>() {

    private val mainViewModel: MainViewModel by viewModels()

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFriendDetailBinding {
        return FragmentFriendDetailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainSharedViewModel.pagingData.observe(viewLifecycleOwner) {
            Log.d("Tag2", "${it}")
        }

        lifecycleScope.launch {
            mainViewModel.allPostData.collect {
                Log.d("Tag1", "${it.size}")
            }
        }

        getFriendList()
        initView()
        initRv()

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

        lifecycleScope.launch {
            mainSharedViewModel.userData.observe(viewLifecycleOwner) { userData ->
                val currentUserUid = CurrentUser.userData?.uid
                val receiveUid = userData?.uid
                val requestId = UUID.randomUUID().toString()

                if (userData != null) {
                    binding.tvFDName.text = userData.name
                    binding.tvFDEmail.text = userData.email
                    if (userData.intro == "") {
                        binding.tvFDIntro.text = "한줄 소개"
                    } else {
                        binding.tvFDIntro.text = userData.intro
                    }
                    if (userData.profileImage != null) {
                        binding.ivFDProfile.clipToOutline = true
                        Glide.with(requireContext()).load(userData.profileImage)
                            .into(binding.ivFDProfile)
                    }
                    if (userData.uid == currentUserUid) {
                        binding.btnFDEditProfile.visibility = View.VISIBLE
                        binding.btnFDFriendList.visibility = View.VISIBLE
                        binding.btnFDFriendRequest.visibility = View.INVISIBLE
                        binding.btnFDSendDM.visibility = View.INVISIBLE
                    } else {
                        binding.btnFDFriendRequest.visibility = View.VISIBLE
                        binding.btnFDSendDM.visibility = View.VISIBLE
                        binding.btnFDEditProfile.visibility = View.INVISIBLE
                        binding.btnFDFriendList.visibility = View.INVISIBLE
                    }
                    checkFriend(userData)
                }

                binding.btnFDFriendCancel.setOnClickListener {

                }

                binding.btnFDFriendRequest.setOnClickListener {
                    if (currentUserUid != null && receiveUid != null) {
                        mainSharedViewModel.requestFriend(requestId, currentUserUid, receiveUid)
                        collectAcceptFlow()
                    }
                }
                binding.btnFDFriendDelete.setOnClickListener {
                    if (currentUserUid != null && receiveUid != null) {
                        mainSharedViewModel.deleteFriend(currentUserUid, receiveUid)
                        collectDeleteFlow()
                    }
                }
            }
            binding.ivFDBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun checkFriend(userData: UserDataModel) {
        lifecycleScope.launch {
            mainSharedViewModel.friendList.observe(viewLifecycleOwner) { friendList ->
                if (friendList.contains(userData)) {
                    binding.btnFDFriendDelete.visibility = View.VISIBLE
                    binding.btnFDFriendRequest.visibility = View.INVISIBLE
                } else {
                    binding.btnFDFriendRequest.visibility = View.VISIBLE
                    binding.btnFDFriendDelete.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun collectAcceptFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.requestFriendResult.collect { result ->
                if (result == true) {
                    Toast.makeText(requireActivity(), "친구 요청 성공!", Toast.LENGTH_SHORT).show()
                } else if (result == false){
                    Toast.makeText(requireActivity(), "친구 요청 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun collectDeleteFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.deleteFriendResult.collect { result ->
                if (result == true) {
                    Toast.makeText(requireActivity(), "친구 삭제 성공!", Toast.LENGTH_SHORT).show()
                } else if (result == false){
                    Toast.makeText(requireActivity(), "친구 삭제 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initRv() {
        val postAdapter = FriendPostAdapter { data ->
            sendData(data)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.postList.collect { postData ->
                val postNumber = postData.size
                val postByCreatedAt = postData.sortedByDescending { it.createdAt }

                binding.tvFDNumber.text = postNumber.toString()
                postAdapter.submitList(postByCreatedAt)

                if (postData.isEmpty()) {
                    binding.rvFD.visibility = View.GONE
                } else {
                    binding.rvFD.visibility = View.VISIBLE
                }
                with(binding.rvFD) {
                    layoutManager = GridLayoutManager(requireContext(), 3)
                    adapter = postAdapter
                }
            }
        }
    }

    private fun sendData(postData: PostDataModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.getPostData(postData)
        }
        findNavController().navigate(R.id.postDetailFragment)
    }

}