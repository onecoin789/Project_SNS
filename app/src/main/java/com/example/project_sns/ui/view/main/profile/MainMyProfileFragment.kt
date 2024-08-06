package com.example.project_sns.ui.view.main.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentMyProfileBinding
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.view.detail.PostDetailFragment
import com.example.project_sns.ui.view.main.MainViewModel
import com.example.project_sns.ui.view.model.PostDataModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainMyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
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
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)

        initView()
        navigateView()
        initRV()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {

        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.getCurrentUserData()
        }
        val userData = CurrentUser.userData
        Log.d("userdata", "${userData?.name}")

        if (userData != null) {
            binding.tvMyName.text = userData.name
            binding.tvMyEmail.text = userData.email
            if (userData.profileImage != null) {
                binding.ivMyProfile.clipToOutline = true
                Glide.with(requireContext()).load(userData.profileImage).into(binding.ivMyProfile)
            }
        }
    }

    private fun initRV() {
        val uid = CurrentUser.userData?.uid.toString()
        val listAdapter = MyProfilePostAdapter { data ->
            sendData(data)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            myProfileViewModel.postInformation.collect {
                val postNumber = it.size
                binding.tvMyNumber.text = postNumber.toString()
                myProfileViewModel.getPost(uid)
                listAdapter.submitList(it)
                if (it.isEmpty()) {
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
        val fragment = PostDetailFragment()
        val bundle = Bundle()
        bundle.putParcelable("postData", postData)
        fragment.arguments = bundle
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