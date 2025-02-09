package com.example.project_sns.ui.view.main.comment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentReCommentListBinding
import com.example.project_sns.ui.BaseDialog
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.util.dateFormat
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.model.CommentModel
import com.example.project_sns.ui.model.ReCommentDataModel
import com.example.project_sns.ui.model.ReCommentModel
import com.example.project_sns.ui.util.notTouch
import com.example.project_sns.ui.util.touch
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

@AndroidEntryPoint
class ReCommentListFragment : BaseFragment<FragmentReCommentListBinding>() {

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private val commentViewModel: CommentViewModel by viewModels()

    private var reCommentData: ReCommentDataModel? = null

    private var reCommentList: MutableList<ReCommentModel?> = mutableListOf()

    private var commentId: String = ""

    private lateinit var listAdapter: ReCommentAdapter


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentReCommentListBinding {
        return FragmentReCommentListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initRv()
        initReComment()
        getReCommentData()
        checkReCommentData()
        collectReCommentChangeResult()

        Log.d("reComment_lastVisible", "${commentViewModel.reCommentLastVisibleItem.value}")

    }

    private fun checkReCommentData() {
        mainSharedViewModel.selectedCommentData.observe(viewLifecycleOwner) { item ->
            if (item != null) {
                commentViewModel.getReCommentChangeResult(item.commentData.commentId)
            }
        }
    }

    private fun collectReCommentChangeResult() {
        commentViewModel.reCommentChangeResult.observe(viewLifecycleOwner) { result ->
            Log.d("reComment_result", "$result")
            if (result == true) {
                commentViewModel.reCommentLastVisibleItem.value = 0
                getReCommentData()
            } else {
                binding.clReCommentRvItem.visibility = View.GONE
                binding.tvReCommentNone.visibility = View.VISIBLE
                binding.tvReCommentSuggest.visibility = View.VISIBLE
            }
        }
    }

    private fun getReCommentData() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.selectedCommentData.observe(viewLifecycleOwner) { item ->
                if (item != null) {
                    Log.d("reComment_comment_id", item.commentData.commentId)
                    commentViewModel.getReComment(
                        item.commentData.commentId,
                        commentViewModel.reCommentLastVisibleItem
                    )
                }
            }
        }
    }


    private fun initView() {
        mainSharedViewModel.selectedCommentData.observe(viewLifecycleOwner) { commentData ->
            if (commentData != null) {

                commentId = commentData.commentData.commentId

                binding.ivReCommentProfile.clipToOutline = true
                if (commentData.userData.profileImage != null) {
                    Glide.with(this).load(commentData.userData.profileImage)
                        .into(binding.ivReCommentProfile)
                } else {
                    Glide.with(this).load(R.drawable.ic_user_fill).into(binding.ivReCommentProfile)
                }

                binding.tvReCommentCommentName.text = commentData.userData.name
                binding.tvReCommentCommentEmail.text = commentData.userData.email
                binding.tvReCommentComment.text = commentData.commentData.comment

                if (commentData.userData.uid == CurrentUser.userData?.uid) {
                    binding.tvReCommentCommentEdit.visibility = View.INVISIBLE
                    binding.tvReCommentCommentDelete.visibility = View.INVISIBLE
                } else {
                    binding.tvReCommentCommentEdit.visibility = View.INVISIBLE
                    binding.tvReCommentCommentDelete.visibility = View.INVISIBLE
                }
            }
        }
        binding.ivReCommentBack.setOnClickListener {
            mainSharedViewModel.selectedCommentData.observe(viewLifecycleOwner) { item ->
                Log.d("reComment_commentData", "${item?.commentData}")

            }
            commentViewModel.clearReCommentListData()
            mainSharedViewModel.clearSelectCommentData()
            mainSharedViewModel.prevPage()
        }
    }

    private fun initRv() {
        listAdapter = ReCommentAdapter(object : ReCommentAdapter.ReCommentItemClick {

            override fun onClickEdit(item: ReCommentModel) {
                initReComment()
                val editComment = binding.etReComment
                editComment.setText(item.reCommentData.comment)
                editReComment(item.reCommentData)
            }

            override fun onClickDelete(item: ReCommentModel) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val dialog = BaseDialog("댓글을 삭제 하시겠습니까?", "삭제하시면 되돌릴 수 없습니다!")
                    dialog.setButtonClickListener(object : BaseDialog.DialogClickEvent {
                        override fun onClickConfirm() {
                            getDeleteItem(item)
                        }
                    })
                    dialog.show(childFragmentManager, "dialog")
                }
            }
        })

        val linearLayoutManager = LinearLayoutManager(requireContext())
        with(binding.rvReComment) {
            layoutManager = linearLayoutManager
            adapter = listAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val lastVisible = linearLayoutManager.findLastVisibleItemPosition().plus(1)
                    if (!binding.rvReComment.canScrollVertically(1)) {
                        moreItem(lastVisible)
                        Log.d(
                            "test_comment",
                            "${commentViewModel.reCommentLastVisibleItem.value}, $lastVisible"
                        )
                    }
                }
            })
        }

        commentViewModel.reCommentListData.observe(viewLifecycleOwner) { reCommentData ->
            Log.d("reComment_data", "${reCommentData.size}")
            val dataSet = reCommentData.sortedByDescending { it.reCommentData.commentAt }
            reCommentList = dataSet.toMutableList()
            listAdapter.submitList(reCommentList)

            if (reCommentData.isNotEmpty()) {
                binding.clReCommentRvItem.visibility = View.VISIBLE
                binding.tvReCommentNone.visibility = View.INVISIBLE
                binding.tvReCommentSuggest.visibility = View.INVISIBLE
            }
        }
    }

    private fun moreItem(lastVisible: Int) {
        val mRecyclerView = binding.rvReComment
//        val runnable = kotlinx.coroutines.Runnable {
//            reCommentList.add(null)
//            listAdapter.notifyItemInserted(reCommentList.size - 1)
//            Log.d("rv_reComment", "$reCommentList")
//        }
//        mRecyclerView.post(runnable)

        CoroutineScope(Dispatchers.Main).launch {
            notTouch(activity)
            val runnableMore = kotlinx.coroutines.Runnable {
//                reCommentList.removeAt(reCommentList.size - 1)
//                listAdapter.notifyItemRemoved(reCommentList.size)
                commentViewModel.reCommentLastVisibleItem.value = lastVisible
            }
            delay(300)
            touch(activity)
            runnableMore.run()
        }
    }

    private fun getDeleteItem(item: ReCommentModel) {
        CoroutineScope(Dispatchers.Main).launch {
            binding.rvReComment.visibility = View.GONE
            binding.pbReComment.visibility = View.VISIBLE
            deleteReCommentData(item)
            delay(300)
            commentViewModel.clearReCommentListData()
            binding.tvReCommentNone.visibility = View.GONE
            binding.tvReCommentSuggest.visibility = View.GONE
            delay(300)
            val runnableRefresh = kotlinx.coroutines.Runnable {
                getReCommentData()
            }
            runnableRefresh.run()
            delay(300)
            binding.rvReComment.visibility = View.VISIBLE
            binding.pbReComment.visibility = View.GONE
        }
    }

    private fun getNewItem() {
        CoroutineScope(Dispatchers.Main).launch {
            binding.rvReComment.visibility = View.GONE
            binding.pbReComment.visibility = View.VISIBLE
            delay(100)
            commentViewModel.clearReCommentListData()
            binding.tvReCommentNone.visibility = View.GONE
            binding.tvReCommentSuggest.visibility = View.GONE
            delay(100)
            val runnableRefresh = kotlinx.coroutines.Runnable {
                getReCommentData()
            }
            runnableRefresh.run()
            delay(100)
            binding.rvReComment.visibility = View.VISIBLE
            binding.pbReComment.visibility = View.GONE
        }
    }

    private fun deleteReCommentData(item: ReCommentModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            commentViewModel.deleteReComment(
                item.reCommentData.commentId,
                item.reCommentData.reCommentId
            )
        }
    }

    private fun initReComment() {
        binding.btnReCommentUp.setOnClickListener {
            if (binding.etReComment.text.isEmpty()) {
                Toast.makeText(requireContext(), "댓글을 입력해주세요!", Toast.LENGTH_SHORT).show()
            } else {
                initReCommentData()
                collectReCommentFlow()
                reCommentData = null
            }
        }
    }

    private fun editReComment(item: ReCommentDataModel) {
        binding.btnReCommentUp.setOnClickListener {
            if (binding.etReComment.text.isEmpty()) {
                Toast.makeText(requireContext(), "댓글을 입력해주세요!", Toast.LENGTH_SHORT).show()
            } else {
                initEditReCommentData(item)
                collectEditReCommentFlow()
                reCommentData = null
            }
        }
    }

    private fun collectReCommentFlow() {
        commentViewModel.reCommentData.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.etReComment.text.clear()
                getNewItem()
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

    private fun collectEditReCommentFlow() {
        commentViewModel.reCommentData.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.etReComment.text.clear()
                getNewItem()
                Toast.makeText(requireContext(), "댓글이 수정되었습니다.", Toast.LENGTH_SHORT)
                    .show()
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

    private fun initReCommentData() {
        val currentUer = CurrentUser.userData
        if (currentUer != null) {
            val reCommentId = UUID.randomUUID().toString()
            val uid = currentUer.uid
            val comment = binding.etReComment.text.toString()
            val time = LocalDateTime.now()
            val commentAt = dateFormat(time)


            reCommentData = ReCommentDataModel(
                uid = uid,
                commentId = commentId,
                reCommentId = reCommentId,
                comment = comment,
                commentAt = commentAt,
                editedAt = null
            )

            commentViewModel.uploadReComment(
                reCommentData
            )

        }
    }

    private fun initEditReCommentData(item: ReCommentDataModel) {
        val editComment = binding.etReComment.text.toString()
        val time = dateFormat(LocalDateTime.now())

        reCommentData = ReCommentDataModel(
            uid = item.uid,
            commentId = item.commentId,
            reCommentId = item.reCommentId,
            comment = editComment,
            commentAt = item.commentAt,
            editedAt = time
        )
        commentViewModel.uploadReComment(reCommentData)
    }
}