package com.example.project_sns.ui.view.main.profile.detail

import android.os.Bundle
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
import com.example.project_sns.ui.model.PostDataModel
import com.example.project_sns.ui.util.sharePost
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.main.profile.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailFragment : BaseFragment<FragmentPostDetailBinding>() {

    private val postViewModel: PostViewModel by viewModels()

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()


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
        val currentUser = CurrentUser.userData
        val profile = binding.ivPDUser
        val likeButton = binding.ivPDHeart


        binding.ivPDHeart.setOnClickListener {
            initLike()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.postData.observe(viewLifecycleOwner) { postData ->
                if (postData != null) {
                    postViewModel.getPostDataByPostId(postData.postId)
                }
            }
        }

        postViewModel.postData.observe(viewLifecycleOwner) { postData ->
            if (postData != null) {
                val user = postData.userData
                val post = postData.postData

                if (post.likePost.isNotEmpty()) {
                    binding.tvPDHeartCount.text = post.likePost.size.toString()
                    binding.tvPDHeartCount.visibility = View.VISIBLE
                } else {
                    binding.tvPDHeartCount.visibility = View.GONE
                }

                if (post.likePost.contains(currentUser?.uid)) {
                    binding.ivPDHeart.tag = "1"
                } else {
                    binding.ivPDHeart.tag = "0"
                }

                when(likeButton.tag) {
                    "0" -> {
                        likeButton.setImageResource(R.drawable.ic_heart)
                    }
                    "1" -> {
                        likeButton.setImageResource(R.drawable.ic_heart_fill)
                    }
                }

                if (currentUser?.profileImage != null) {
                    profile.clipToOutline = true
                    Glide.with(requireContext()).load(user.profileImage).into(profile)
                } else {
                    Glide.with(requireContext()).load(R.drawable.ic_user_fill).into(profile)
                }

                if (post.mapData?.placeName != null) {
                    binding.tvPDLocation.text = post.mapData.placeName
                } else {
                    binding.tvPDLocation.visibility = View.GONE
                }

                binding.tvPDName.text = user.name
                binding.tvPDEmail.text = user.email
                binding.tvPDPostText.text = post.postText

                if (post.editedAt != null) {
                    binding.tvPDEdit.visibility = View.VISIBLE
                } else {
                    binding.tvPDEdit.visibility = View.GONE
                }

                initRv(post)

                if (post.imageList?.size == 1) {
                    binding.idcPD.visibility = View.GONE
                } else {
                    binding.idcPD.visibility = View.VISIBLE
                }

                binding.ivPDShare.setOnClickListener {
                    startActivity(sharePost(post.postText.toString()))
                }

                binding.ivPDBack.setOnClickListener {
                    backButton()
                }

                binding.ivPDMore.setOnClickListener {
                    findNavController().navigate(R.id.action_postDetailFragment_to_postDetailSelectBottomFragment)
                }

                binding.tvPDComment.setOnClickListener {
                    findNavController().navigate(R.id.commentFragment)
                }
            }
        }
    }

    private fun initLike() {
        val likeButton = binding.ivPDHeart

        when (likeButton.tag) {
            "0" -> {
                binding.ivPDHeart.tag = "1"
                binding.ivPDHeart.setImageResource(R.drawable.ic_heart_fill)
                mainSharedViewModel.postData.observe(viewLifecycleOwner) {
                    if (it != null) {
                        postViewModel.updateLike(it.postId, true)
                    }
                }
            }

            "1" -> {
                binding.ivPDHeart.tag = "0"
                binding.ivPDHeart.setImageResource(R.drawable.ic_heart)
                mainSharedViewModel.postData.observe(viewLifecycleOwner) {
                    if (it != null) {
                        postViewModel.updateLike(it.postId, false)
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


