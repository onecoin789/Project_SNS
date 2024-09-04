package com.example.project_sns.ui.view.main.comment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_sns.databinding.FragmentCommentBinding
import com.example.project_sns.ui.BaseBottomSheet
import com.example.project_sns.ui.CurrentPost
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.util.dateFormat
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.model.CommentDataModel
import com.example.project_sns.ui.view.model.ReCommentDataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.w3c.dom.Comment
import java.time.LocalDateTime
import java.util.UUID

@AndroidEntryPoint
class CommentFragment : BaseBottomSheet<FragmentCommentBinding>() {

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private var tag: Boolean? = null

    private var reCommentData: ReCommentDataModel? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCommentBinding {
        return FragmentCommentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCommentData()
        initComment()
        initRv()
        initView()

    }

    private fun initView() {
        binding.ivCommentInvisible.setOnClickListener {
            binding.clCommentTag.visibility = View.GONE
            initComment()
        }
    }

    private fun getCommentData() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.postData.observe(viewLifecycleOwner) { postData ->
                if (postData != null) {
                    mainSharedViewModel.getComment(postData.postId)
                }
            }
        }
    }


    private fun initReComment() {
        tag = binding.clCommentTag.isVisible
        if (tag == true) {
            binding.btnCommentUp.setOnClickListener {
                if (binding.etComment.text.isEmpty()) {
                    Toast.makeText(requireContext(), "댓글을 입력해주세요!", Toast.LENGTH_SHORT).show()
                } else {
                    initReCommentData()
                    collectReCommentFlow()
                    binding.clCommentTag.visibility = View.GONE
                    initComment()
                    reCommentData = null
                }
            }
        }
    }

    private fun initComment() {
        tag = binding.clCommentTag.isInvisible
        if (tag == false) {
            binding.btnCommentUp.setOnClickListener {
                if (binding.etComment.text.isEmpty()) {
                    Toast.makeText(requireContext(), "댓글을 입력해주세요!", Toast.LENGTH_SHORT).show()
                } else {
                    initData()
                    collectCommentFlow()
                }
            }
        }
    }

    private fun initRv() {

        val listAdapter = CommentAdapter(object : CommentAdapter.CommentItemClick {
            override fun onClick(item: CommentDataModel) {
                viewLifecycleOwner.lifecycleScope.launch {
                    mainSharedViewModel.getCommentData(item)
                }
                binding.tvCommentTag.text = item.name
                binding.clCommentTag.visibility = View.VISIBLE
                initReComment()
            }

            override fun onClickDelete(item: CommentDataModel) {
                viewLifecycleOwner.lifecycleScope.launch {
                    mainSharedViewModel.postData.observe(viewLifecycleOwner) { currentPostData ->
                        if (currentPostData != null) {
                            mainSharedViewModel.getCommentData(item)
                            mainSharedViewModel.getReComment(currentPostData.postId, item.commentId)
                            inflateCommentDialog("댓글 삭제", "댓글을 삭제할까요?")
                        }
                    }
                }
            }

            override fun onClickList(item: CommentDataModel) {
                viewLifecycleOwner.lifecycleScope.launch {
                    mainSharedViewModel.postData.observe(viewLifecycleOwner) { currentPostData ->
                        if (currentPostData != null) {
                            mainSharedViewModel.getCommentData(item)
                            mainSharedViewModel.getReComment(
                                currentPostData.postId,
                                item.commentId
                            )
                        }
                    }
                }
            }

            override fun onClickReCommentDelete(item: ReCommentDataModel) {
                viewLifecycleOwner.lifecycleScope.launch {
                    mainSharedViewModel.getReCommentData(item)
                    inflateReCommentDialog("댓글 삭제", "댓글을 삭제할까요?")
                }

            }

        })
        with(binding.rvComment) {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = listAdapter
        }
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.commentListData.collect { data ->
                val dataSet = data.sortedByDescending { it.commentAt }
                listAdapter.submitList(dataSet)
                if (data.isEmpty()) {
                    binding.tvCommentNone.visibility = View.VISIBLE
                    binding.tvCommentSuggest.visibility = View.VISIBLE
                    binding.rvComment.visibility = View.INVISIBLE
                } else {
                    binding.rvComment.visibility = View.VISIBLE
                    binding.tvCommentNone.visibility = View.INVISIBLE
                    binding.tvCommentSuggest.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun collectReCommentFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                mainSharedViewModel.reCommentData.collect {
                    if (it == true) {
                        binding.etComment.text.clear()
                    } else if (it == false) {
                        Toast.makeText(
                            requireContext(),
                            "잠시 후 다시 시도해주세요",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        }
    }

    private fun collectCommentFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                mainSharedViewModel.commentData.collect {
                    if (it == true) {
                        binding.etComment.text.clear()
                    } else if (it == false) {
                        Toast.makeText(requireContext(), "잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun initData() {
        val currentUser = CurrentUser.userData
        if (currentUser != null) {
            val commentId = UUID.randomUUID().toString()
            val uid = currentUser.uid
            val name = currentUser.name
            val email = currentUser.email
            val profileImage = currentUser.profileImage
            val comment = binding.etComment.text.toString()
            val time = LocalDateTime.now()
            val commentAt = dateFormat(time)

            val data = CommentDataModel(
                commentId = commentId,
                comment = comment,
                commentAt = commentAt,
                uid = uid,
                name = name,
                email = email,
                profileImage = profileImage,
                reCommentData = null
            )
            viewLifecycleOwner.lifecycleScope.launch {
                mainSharedViewModel.postData.observe(viewLifecycleOwner) { postData ->
                    if (postData != null) {
                        mainSharedViewModel.uploadComment(postData.postId, data)
                    }
                }
            }
        }
    }

    private fun initReCommentData() {
        val currentUer = CurrentUser.userData
        if (currentUer != null) {
            val commentId = UUID.randomUUID().toString()
            val uid = currentUer.uid
            val name = currentUer.name
            val email = currentUer.email
            val profileImage = currentUer.profileImage
            val comment = binding.etComment.text.toString()
            val time = LocalDateTime.now()
            val commentAt = dateFormat(time)


            reCommentData = ReCommentDataModel(
                commentId = commentId,
                comment = comment,
                commentAt = commentAt,
                uid = uid,
                name = name,
                email = email,
                profileImage = profileImage
            )

            viewLifecycleOwner.lifecycleScope.launch {
                mainSharedViewModel.selectedCommentData.observe(viewLifecycleOwner) { commentData ->
                    Log.d("test_viewModel", "${commentData?.commentId}")
                    if (commentData != null) {
                        CurrentPost.postData?.let {
                            mainSharedViewModel.uploadReComment(
                                it.postId,
                                commentData.commentId,
                                reCommentData
                            )
                        }
                    }
                }
            }
        }
    }
}
