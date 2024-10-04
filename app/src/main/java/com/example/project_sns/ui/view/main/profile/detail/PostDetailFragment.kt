package com.example.project_sns.ui.view.main.profile.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentPostDetailBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.mapper.toViewType
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.main.MainViewModel
import com.example.project_sns.ui.view.model.PostDataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailFragment : BaseFragment<FragmentPostDetailBinding>() {

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()



    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPostDetailBinding {
        return FragmentPostDetailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            mainSharedViewModel.postList.collect {
                Log.d("Tag2", "${it.size}")
            }
        }

        initView()
    }

    private fun initView() {
        val currentUser = CurrentUser.userData
        val profile = binding.ivPDUser

        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.postData.observe(viewLifecycleOwner) { postData ->

                if (postData != null) {
                    if (currentUser?.profileImage != null) {
                        profile.clipToOutline = true
                        Glide.with(requireContext()).load(currentUser.profileImage).into(profile)
                    } else {
                        Glide.with(requireContext()).load(R.drawable.ic_user_fill).into(profile)
                    }

                    if (postData.mapData?.placeName != null) {
                        binding.tvPDLocation.text = postData.mapData.placeName
                    }

                    binding.tvPDName.text = currentUser?.name
                    binding.tvPDEmail.text = currentUser?.email
                    binding.tvPDPostText.text = postData.postText

                    if (postData.editedAt != null) {
                        binding.tvPDEdit.visibility = View.VISIBLE
                    } else {
                        binding.tvPDEdit.visibility = View.GONE
                    }

                    initRv(postData)

                    if (postData.imageList?.size == 1) {
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

                    binding.tvPDComment.setOnClickListener {
                        findNavController().navigate(R.id.commentFragment)
                    }
                }
            }
        }
    }


    private fun initRv(postData: PostDataModel?) {
        val imageAdapter = PostImageAdapter()
        val indicator = binding.idcPD
        with(binding.vpPDPostImage) {
            adapter = imageAdapter
        }
        indicator.attachTo(binding.vpPDPostImage)
        val listViewType = postData?.imageList?.map { it.toViewType("video") }
        imageAdapter.submitList(listViewType)
    }
}

