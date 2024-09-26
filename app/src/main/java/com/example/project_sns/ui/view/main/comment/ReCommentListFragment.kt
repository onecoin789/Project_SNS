package com.example.project_sns.ui.view.main.comment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentReCommentListBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.util.dateFormat
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.model.CommentModel
import com.example.project_sns.ui.view.model.ReCommentDataModel
import com.example.project_sns.ui.view.model.ReCommentModel
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

    private var reCommentData: ReCommentDataModel? = null

    private var reCommentList: MutableList<ReCommentModel?> = mutableListOf()

    private lateinit var listAdapter: ReCommentAdapter

    val reCommentLastVisibleItem = MutableStateFlow(0)

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentReCommentListBinding {
        return FragmentReCommentListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clearData()
        initView()
        initRv()
        initReComment()
    }

    private fun clearData() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.resetReCommentData()
        }
    }

    private fun initView() {
        mainSharedViewModel.selectedCommentData.observe(viewLifecycleOwner) { commentData ->
            if (commentData != null) {
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
                    binding.tvReCommentCommentEdit.visibility = View.VISIBLE
                    binding.tvReCommentCommentDelete.visibility = View.VISIBLE
                } else {
                    binding.tvReCommentCommentEdit.visibility = View.GONE
                    binding.tvReCommentCommentDelete.visibility = View.GONE
                }
            }
        }
        binding.ivReCommentBack.setOnClickListener {
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
                    mainSharedViewModel.getReCommentData(item.reCommentData)
                    inflateReCommentDialog("댓글 삭제", "댓글을 삭제할까요?")
                }
            }
        })

        val linearLayoutManager = LinearLayoutManager(requireContext())
        with(binding.rvReComment) {
            layoutManager = linearLayoutManager
            adapter = listAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisible = linearLayoutManager.findLastVisibleItemPosition().plus(1)
                    if (!binding.rvReComment.canScrollVertically(1)) {
                        binding.clReCommentMore.setOnClickListener {
                            moreItem(lastVisible)
                        }
                        Log.d("test_comment", "${reCommentLastVisibleItem.value}, $lastVisible")
                    }
                }
            })
        }

        mainSharedViewModel.reCommentListData.observe(viewLifecycleOwner) { reCommentData ->
            val dataSet = reCommentData.sortedByDescending { it.reCommentData.commentAt }

            reCommentList = dataSet.toMutableList()
            listAdapter.submitList(dataSet)
            listAdapter.notifyItemInserted(reCommentData.size - 1)

            if (reCommentData.isEmpty()) {
                binding.tvReCommentNone.visibility = View.VISIBLE
                binding.tvReCommentSuggest.visibility = View.VISIBLE
                binding.clReCommentRvItem.visibility = View.GONE
            } else {
                binding.clReCommentRvItem.visibility = View.VISIBLE
                binding.tvReCommentNone.visibility = View.INVISIBLE
                binding.tvReCommentSuggest.visibility = View.INVISIBLE
            }
        }
    }

    private fun moreItem(lastVisible: Int) {
        val mRecyclerView = binding.rvReComment
        val runnable = kotlinx.coroutines.Runnable {
            reCommentList.add(null)
            listAdapter.notifyItemInserted(reCommentList.size - 1)
        }
        mRecyclerView.post(runnable)

        CoroutineScope(Dispatchers.Main).launch {
            val runnableMore = kotlinx.coroutines.Runnable {
                reCommentList.removeAt(reCommentList.size - 1)
                listAdapter.notifyItemRemoved(reCommentList.size)
                reCommentLastVisibleItem.value = lastVisible
            }
            delay(1000)
            runnableMore.run()
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
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                mainSharedViewModel.reCommentData.collect {
                    if (it == true) {
                        binding.etReComment.text.clear()
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

    private fun collectEditReCommentFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                mainSharedViewModel.reCommentData.collect {
                    if (it == true) {
                        binding.etReComment.text.clear()
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


            viewLifecycleOwner.lifecycleScope.launch {
                mainSharedViewModel.selectedCommentData.observe(viewLifecycleOwner) { commentData ->
                    if (commentData != null) {
                        reCommentData = ReCommentDataModel(
                            uid = uid,
                            commentId = commentData.commentData.commentId,
                            reCommentId = reCommentId,
                            comment = comment,
                            commentAt = commentAt,
                            editedAt = null
                        )
                        mainSharedViewModel.uploadReComment(
                            reCommentData
                        )
                    }
                }
            }
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

        viewLifecycleOwner.lifecycleScope.launch {
            mainSharedViewModel.selectedCommentData.observe(viewLifecycleOwner) { commentData ->
                if (commentData != null) {
                    mainSharedViewModel.uploadReComment(
                        reCommentData
                    )
                }
            }
        }
    }
}