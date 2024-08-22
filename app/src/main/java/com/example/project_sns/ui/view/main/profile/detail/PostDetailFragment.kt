package com.example.project_sns.ui.view.main.profile.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentPostDetailBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.view.main.profile.MyProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailFragment : BaseFragment<FragmentPostDetailBinding>() {

    private val myProfileViewModel: MyProfileViewModel by activityViewModels()

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
        val profile = binding.ivPDUser

        viewLifecycleOwner.lifecycleScope.launch {
            myProfileViewModel.postData.observe(viewLifecycleOwner) { postData ->

                if (postData != null) {
                    if (postData.profileImage != null) {
                        profile.clipToOutline = true
                        Glide.with(requireContext()).load(postData.profileImage).into(profile)
                    } else {
                        Glide.with(requireContext()).load(R.drawable.ic_user_fill).into(profile)
                    }

                    if (postData.mapData?.placeName != null) {
                        binding.tvPDLocation.text = postData.mapData.placeName
                    }

                    binding.tvPDName.text = postData.name
                    binding.tvPDEmail.text = postData.email
                    binding.tvPDPostText.text = postData.postText

                    initRv(postData.image)

                    if (postData.image?.size == 1) {
                        binding.idcPD.visibility = View.INVISIBLE
                    } else {
                        binding.idcPD.visibility = View.VISIBLE
                    }

                    binding.ivPDBack.setOnClickListener {
                        backButton()
                    }

                    binding.ivPDMore.setOnClickListener {
                        findNavController().navigate(R.id.action_postDetailFragment_to_postDetailSelectBottomFragment)
                    }

                    binding.ivPDHeart.setOnClickListener {

                    }
                }
            }
        }
    }

    private fun initRv(uriList: List<String>?) {
        val imageAdapter = PostImageAdapter()
        val indicator = binding.idcPD
        imageAdapter.submitList(uriList)
        with(binding.vpPDPostImage) {
            adapter = imageAdapter
        }
        indicator.attachTo(binding.vpPDPostImage)
    }
}
