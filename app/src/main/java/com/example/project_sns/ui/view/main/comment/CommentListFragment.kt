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
import androidx.recyclerview.widget.RecyclerView
import com.example.project_sns.databinding.FragmentCommentListBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.util.dateFormat
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.model.CommentDataModel
import com.example.project_sns.ui.view.model.CommentModel
import com.example.project_sns.ui.view.model.ReCommentDataModel
import com.example.project_sns.ui.view.model.ReCommentModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

@AndroidEntryPoint
class CommentListFragment : BaseFragment<FragmentCommentListBinding>() {

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private var commentData: CommentDataModel? = null

    val commentLastVisibleItem = MutableStateFlow(0)

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCommentListBinding {
        return FragmentCommentListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCommentData()
        initComment()
        initRv()
    }


    private fun getCommentData() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.postData.observe(viewLifecycleOwner) { postData ->
                if (postData != null) {
                    mainSharedViewModel.getComment(postData.postId, commentLastVisibleItem)
                }
            }
        }
    }


    private fun initComment() {
        binding.btnCommentUp.setOnClickListener {
            if (binding.etComment.text.isEmpty()) {
                Toast.makeText(requireContext(), "댓글을 입력해주세요!", Toast.LENGTH_SHORT).show()
            } else {
                initCommentData()
                collectCommentFlow()
            }
        }
    }

    private fun editComment(item: CommentDataModel) {
        binding.btnCommentUp.setOnClickListener {
            if (binding.etComment.text.isEmpty()) {
                Toast.makeText(requireContext(), "댓글을 입력해주세요!", Toast.LENGTH_SHORT).show()
            } else {
                initEditCommentData(item)
                collectEditCommentFlow()
            }
        }
    }


    private fun initRv() {
        val listAdapter = CommentAdapter(object : CommentAdapter.CommentItemClick {

            override fun onClickCommentEdit(item: CommentModel) {
                val editComment = binding.etComment
                editComment.setText(item.commentData.comment)
                editComment(item.commentData)
            }

            override fun onClickCommentDelete(item: CommentModel) {
                viewLifecycleOwner.lifecycleScope.launch {
                    mainSharedViewModel.getCommentData(item)
                    inflateCommentDialog("댓글 삭제", "댓글을 삭제할까요?")
                }
            }

            override fun onClickReCommentList(item: CommentModel) {
                viewLifecycleOwner.lifecycleScope.launch {
                    mainSharedViewModel.getCommentData(item)
                    mainSharedViewModel.getReComment(
                        item.commentData.commentId,
                        mainSharedViewModel.reCommentLastVisibleItem
                    )
                }
                mainSharedViewModel.nextPage()
            }
        })
        val linearLayoutManager = LinearLayoutManager(requireContext())
        with(binding.rvComment) {
            layoutManager = linearLayoutManager
            adapter = listAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisible = linearLayoutManager.findLastVisibleItemPosition().plus(1)
                    if (!binding.rvComment.canScrollVertically(1)) {
                        commentLastVisibleItem.value = lastVisible
                        Log.d("test_comment", "${commentLastVisibleItem.value}, $lastVisible")
                    }
                }
            })
        }


        mainSharedViewModel.commentListData.observe(viewLifecycleOwner) { data ->
            val dataSet = data.sortedByDescending { it.commentData.commentAt }
            listAdapter.submitList(dataSet)

            if (data.isEmpty()) {
                binding.tvCommentNone.visibility = View.VISIBLE
                binding.tvCommentSuggest.visibility = View.VISIBLE
                binding.rvComment.visibility = View.GONE
            } else {
                binding.rvComment.visibility = View.VISIBLE
                binding.tvCommentNone.visibility = View.INVISIBLE
                binding.tvCommentSuggest.visibility = View.INVISIBLE
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

    private fun collectEditCommentFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                mainSharedViewModel.commentData.collect {
                    if (it == true) {
                        binding.etComment.text.clear()
                        Toast.makeText(requireContext(), "댓글이 수정되었습니다.", Toast.LENGTH_SHORT)
                            .show()
                    } else if (it == false) {
                        Toast.makeText(requireContext(), "잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }


    private fun initCommentData() {
        val currentUser = CurrentUser.userData
        if (currentUser != null) {
            val commentId = UUID.randomUUID().toString()
            val uid = currentUser.uid
            val comment = binding.etComment.text.toString()
            val time = LocalDateTime.now()
            val commentAt = dateFormat(time)

            viewLifecycleOwner.lifecycleScope.launch {
                mainSharedViewModel.postData.observe(viewLifecycleOwner) { postData ->
                    if (postData != null) {
                        commentData = CommentDataModel(
                            uid = uid,
                            postId = postData.postId,
                            commentId = commentId,
                            comment = comment,
                            commentAt = commentAt,
                            editedAt = null,
                            reCommentSize = 0
                        )
                        mainSharedViewModel.uploadComment(commentData)
                    }
                }
            }
        }
    }


    private fun initEditCommentData(item: CommentDataModel) {
        val editComment = binding.etComment.text.toString()
        val time = dateFormat(LocalDateTime.now())

        commentData = CommentDataModel(
            uid = item.uid,
            postId = item.postId,
            commentId = item.commentId,
            comment = editComment,
            commentAt = item.commentAt,
            editedAt = time,
            reCommentSize = item.reCommentSize
        )
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.postData.observe(viewLifecycleOwner) { postData ->
                if (postData != null) {
                    mainSharedViewModel.uploadComment(commentData)
                }
            }
        }
    }
}
