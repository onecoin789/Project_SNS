package com.example.project_sns.ui.view.main.profile.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentPostDetailBinding
import com.example.project_sns.ui.BaseFragment

class PostDetailFragment : BaseFragment<FragmentPostDetailBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPostDetailBinding {
        return FragmentPostDetailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        val args: PostDetailFragmentArgs by navArgs()
        val postData = args.postData
        val profile = binding.ivPDUser

        if (postData.profileImage != null) {
            profile.clipToOutline = true
            Glide.with(requireContext()).load(postData.profileImage).into(profile)
        } else {
            Glide.with(requireContext()).load(R.drawable.ic_user_fill).into(profile)
        }

        if (postData.placeName != null) {
            binding.tvPDLocation.text = postData.placeName
        }

        binding.tvPDName.text = postData.name
        binding.tvPDEmail.text = postData.email
        binding.tvPDPostText.text = postData.postText

        binding.ivPDBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initBottomFragment() {

    }
}
