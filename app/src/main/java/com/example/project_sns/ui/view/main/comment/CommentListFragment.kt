package com.example.project_sns.ui.view.main.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_sns.databinding.FragmentCommentListBinding
import com.example.project_sns.ui.BaseDialog
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.model.CommentDataModel
import com.example.project_sns.ui.model.CommentModel
import com.example.project_sns.ui.util.hideKeyboard
import com.example.project_sns.ui.util.notTouch
import com.example.project_sns.ui.util.postDateFormat
import com.example.project_sns.ui.util.touch
import com.example.project_sns.ui.view.main.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@AndroidEntryPoint
class CommentListFragment : BaseFragment<FragmentCommentListBinding>() {

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private val commentViewModel: CommentViewModel by viewModels()

    private var commentData: CommentDataModel? = null

    private var commentList: MutableList<CommentModel?> = mutableListOf()

    private lateinit var listAdapter: CommentAdapter


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCommentListBinding {
        return FragmentCommentListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initComment()
        initRv()
        checkCommentData()
        collectCommentChangeResult()


    }

    private fun checkCommentData() {
        mainSharedViewModel.postData.observe(viewLifecycleOwner) { postData ->
            if (postData != null) {
                commentViewModel.getCommentChangeResult(postData.postId)
            }
        }
    }

    private fun collectCommentChangeResult() {
        commentViewModel.commentChangeResult.observe(viewLifecycleOwner) { result ->
            if (result == true) {
                mainSharedViewModel.clearCommentData()
                getCommentData()
            } else {
                binding.tvCommentNone.visibility = View.VISIBLE
                binding.tvCommentSuggest.visibility = View.VISIBLE
                binding.clCommentRvItem.visibility = View.GONE
                binding.pbComment.visibility = View.GONE
            }
        }
    }


    private fun getCommentData() {
        mainSharedViewModel.postData.observe(viewLifecycleOwner) { postData ->
            if (postData != null) {
                mainSharedViewModel.getComment(
                    postData.postId,
                    mainSharedViewModel.commentLastVisibleItem
                )
            }
        }
    }


    private fun initComment() {
        binding.btnCommentUp.setOnClickListener {
            if (binding.etComment.text.isEmpty()) {
                Toast.makeText(requireContext(), "댓글을 입력해주세요!", Toast.LENGTH_SHORT).show()
            } else {
                this.hideKeyboard()
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
                this.hideKeyboard()
                initEditCommentData(item)
                collectEditCommentFlow()
            }
        }
    }


    private fun initRv() {
        listAdapter = CommentAdapter(object : CommentAdapter.CommentItemClickListener {

            override fun onClickCommentEdit(item: CommentModel) {
                val editComment = binding.etComment
                editComment.setText(item.commentData.comment)
                editComment(item.commentData)
            }

            override fun onClickCommentDelete(item: CommentModel) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val dialog = BaseDialog("댓글을 삭제 하시겠습니까?", "삭제하시면 되돌릴 수 없습니다!")
                    dialog.setButtonClickListener(object : BaseDialog.DialogClickEvent {
                        override fun onClickConfirm() {
                            getNewItem(item)
                        }
                    })
                    dialog.show(childFragmentManager, "dialog")
                }
            }

            override fun onClickReCommentList(item: CommentModel) {
                mainSharedViewModel.getSelectCommentData(item)
                mainSharedViewModel.nextPage()
            }
        })
        val linearLayoutManager = LinearLayoutManager(requireContext())
        with(binding.rvComment) {
            layoutManager = linearLayoutManager
            adapter = listAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val lastVisible = linearLayoutManager.findLastVisibleItemPosition().plus(1)
                    if (!binding.rvComment.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        moreItem(lastVisible)
                    }
                }
            })
        }


        mainSharedViewModel.commentListData.observe(viewLifecycleOwner) { data ->
            val dataSet = data.sortedByDescending { it.commentData.commentAt }
            commentList = dataSet.toMutableList()
            listAdapter.submitList(commentList)

            if (data.isNotEmpty()) {
                binding.clCommentRvItem.visibility = View.VISIBLE
                binding.tvCommentNone.visibility = View.INVISIBLE
                binding.tvCommentSuggest.visibility = View.INVISIBLE
            }
        }
    }

    private fun moreItem(lastVisible: Int) {
        val mRecyclerView = binding.rvComment
//        val runnable = kotlinx.coroutines.Runnable {
//            commentList.add(null)
//            listAdapter.notifyItemInserted(commentList.size - 1)
//        }
//        mRecyclerView.post(runnable)

        CoroutineScope(Dispatchers.Main).launch {
            notTouch(activity)
            val runnableMore = kotlinx.coroutines.Runnable {
//                commentList.removeAt(commentList.size - 1)
//                listAdapter.notifyItemRemoved(commentList.size)
                mainSharedViewModel.commentLastVisibleItem.value = lastVisible
            }
            delay(300)
            touch(activity)
            runnableMore.run()
        }
    }

    private fun getNewItem(item: CommentModel) {
        binding.pbComment.visibility = View.VISIBLE
        deleteCommentData(item)
        mainSharedViewModel.clearCommentData()
        CoroutineScope(Dispatchers.Main).launch {
            binding.tvCommentNone.visibility = View.GONE
            binding.tvCommentSuggest.visibility = View.GONE
            val runnableRefresh = kotlinx.coroutines.Runnable {
                getCommentData()
            }
            delay(200)
            runnableRefresh.run()
            delay(300)
            binding.pbComment.visibility = View.GONE
            mainSharedViewModel.setNullData()
        }
    }

    private fun deleteCommentData(item: CommentModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.deleteComment(item.commentData.commentId)
        }
    }


    private fun collectCommentFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.commentData.collect {
                if (it == true) {
                    binding.etComment.text.clear()
                    getCommentData()
                } else if (it == false) {
                    Toast.makeText(requireContext(), "잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun collectEditCommentFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.commentData.collect {
                if (it == true) {
                    binding.etComment.text.clear()
                    getCommentData()
                    Toast.makeText(requireContext(), "댓글이 수정되었습니다.", Toast.LENGTH_SHORT)
                        .show()
                } else if (it == false) {
                    Toast.makeText(requireContext(), "잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT)
                        .show()
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
            val commentAt = postDateFormat()


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
        val time = postDateFormat()

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
