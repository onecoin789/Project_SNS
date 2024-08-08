package com.example.project_sns.ui.view.main.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentMainMyProfileBinding
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.util.refreshFragment
import com.example.project_sns.ui.view.main.MainViewModel
import com.example.project_sns.ui.view.main.viewpager.MainFragment
import com.example.project_sns.ui.view.main.viewpager.MainFragmentDirections
import com.example.project_sns.ui.view.model.PostDataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainMyProfileFragment : Fragment() {

    private var _binding: FragmentMainMyProfileBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()

    private val myProfileViewModel: MyProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainMyProfileBinding.inflate(inflater, container, false)

        initView()
        navigateView()
        initRv()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                }
            }
        }
    }


    private fun initRv() {
        val uid = CurrentUser.userData?.uid.toString()
        val listAdapter = MyProfilePostAdapter { data ->
            sendData(data)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            myProfileViewModel.postInformation.collect { data ->
                val postNumber = data.size
                binding.tvMyNumber.text = postNumber.toString()
                myProfileViewModel.getCurrentUserPost(uid)
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
        val action = MainFragmentDirections.actionMainFragmentToPostDetailFragment(postData)
        findNavController().navigate(action)
    }

    private fun navigateView() {
        binding.ivMySetting.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_settingFragment)
        }

        binding.btnMyEdit.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_editFragment)
        }

        binding.btnMyFriend.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_friendFragment)
        }

        binding.ivMyAdd.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_makePostFragment)
        }

        binding.ivMyDelete.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_dialogFragment)
        }

    }
}