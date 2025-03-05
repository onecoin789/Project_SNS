package com.example.project_sns.ui.view.main.profile.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentPostLikeUserBinding
import com.example.project_sns.ui.BaseBottomSheet
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.main.profile.PostViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PostLikeUserFragment : BaseBottomSheet<FragmentPostLikeUserBinding>() {

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private val postViewModel: PostViewModel by viewModels()


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPostLikeUserBinding {
        return FragmentPostLikeUserBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPostLikeUser()
        initView()

    }

    private fun getPostLikeUser() {
        mainSharedViewModel.postData.observe(viewLifecycleOwner) { postData ->
            if (postData != null) {
                postViewModel.getLikeUserData(postData.postId)
            }
        }
    }

    private fun initView() {
        val postLikeUserAdapter = PostLikeUserAdapter(object : PostLikeUserAdapter.LikeUserItemClickListener {

        })
        with(binding.rvPostLike) {
            adapter = postLikeUserAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        postViewModel.likeUserList.observe(viewLifecycleOwner) { userList ->
            if (userList.isNotEmpty()) {
                postLikeUserAdapter.submitList(userList)
                binding.tvPostLikeNull.visibility = View.GONE
            } else {
                binding.tvPostLikeNull.visibility = View.VISIBLE
            }
        }
    }

}