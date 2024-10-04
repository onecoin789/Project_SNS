package com.example.project_sns.ui.view.main.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentMainMyProfileBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.main.MainViewModel
import com.example.project_sns.ui.view.model.PostDataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainMyProfileFragment : BaseFragment<FragmentMainMyProfileBinding>() {

    private val mainViewModel: MainViewModel by viewModels()

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainMyProfileBinding {
       return FragmentMainMyProfileBinding.inflate(inflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            mainSharedViewModel.postList.collect {
                Log.d("Tag2", "${it.size}")
            }
        }
        initView()
        navigateView()
        initRv()
        getPostList()
    }

    private fun getPostList() {
        val currentUser = CurrentUser.userData?.uid
        viewLifecycleOwner.lifecycleScope.launch {
            if (currentUser != null) {
                mainSharedViewModel.getUserPost(currentUser)
            }
        }
    }

    private fun initView() {

        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.getCurrentUserData()
            mainViewModel.currentUserData.collect { userData ->
                if (userData != null) {
                    binding.tvMyName.text = userData.name
                    binding.tvMyEmail.text = userData.email
                    if (userData.profileImage != null) {
                        binding.ivMyProfile.clipToOutline = true
                        Glide.with(requireContext()).load(userData.profileImage)
                            .into(binding.ivMyProfile)
                    }
                    if (userData.intro == "") {
                        binding.tvMyIntro.text = "한줄 소개"
                    } else {
                        binding.tvMyIntro.text = userData.intro
                    }
                    binding.btnMyFriend.setOnClickListener {
                        mainSharedViewModel.getFriendList(userData.uid)
                        findNavController().navigate(R.id.friendFragment)
                    }
                }
            }
        }
    }


    private fun initRv() {
        val listAdapter = MyProfilePostAdapter { data ->
            sendData(data)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.postList.collect { data ->
                val postNumber = data.size
                binding.tvMyNumber.text = postNumber.toString()
                listAdapter.submitList(data.sortedByDescending { it.createdAt })
                if (data.isEmpty()) {
                    binding.tvMyNullPost.visibility = View.VISIBLE
                    binding.rvMyProfile.visibility = View.GONE
                    binding.ivMyLine.visibility = View.GONE
                    binding.tvMyTextPost.visibility = View.INVISIBLE
                    binding.tvMyNumber.visibility = View.INVISIBLE
                    binding.tvMyTextNumber.visibility = View.INVISIBLE
                } else {
                    binding.rvMyProfile.visibility = View.VISIBLE
                    binding.ivMyLine.visibility = View.VISIBLE
                    binding.tvMyNullPost.visibility = View.GONE
                    binding.tvMyTextPost.visibility = View.VISIBLE
                    binding.tvMyNumber.visibility = View.VISIBLE
                    binding.tvMyTextNumber.visibility = View.VISIBLE
                }
            }
        }
        with(binding.rvMyProfile) {
            layoutManager = GridLayoutManager(requireActivity(), 3)
            adapter = listAdapter
        }
    }

    private fun sendData(postData: PostDataModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.getPostData(postData)
        }
        findNavController().navigate(R.id.action_mainFragment_to_postDetailFragment)
    }

    private fun navigateView() {
        binding.ivMySetting.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_settingFragment)
        }

        binding.btnMyEdit.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_editFragment)
        }

        binding.btnMyFriend.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_friendDetailFragment)
        }

        binding.ivMyAdd.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_makePostFragment)
        }

    }
}