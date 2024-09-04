package com.example.project_sns.ui.view.main.comment


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemCommentBinding
import com.example.project_sns.ui.CurrentPost
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.model.CommentDataModel
import com.example.project_sns.ui.view.model.ReCommentDataModel

class CommentAdapter(private val onClick: CommentItemClick) :
    ListAdapter<CommentDataModel, CommentAdapter.CommentViewHolder>(diffUtil) {

    interface CommentItemClick {
        fun onClick(item: CommentDataModel)
        fun onClickDelete(item: CommentDataModel)
        fun onClickList(item: CommentDataModel)
        fun onClickReCommentDelete(item: ReCommentDataModel)

    }


    class CommentViewHolder(
        private val binding: RvItemCommentBinding,
        private val onClick: CommentItemClick
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item: CommentDataModel) {

            if (item.profileImage != null) {
                binding.ivComment.clipToOutline = true
                Glide.with(binding.root).load(item.profileImage).into(binding.ivComment)
            } else {
                Glide.with(binding.root).load(R.drawable.ic_user_fill).into(binding.ivComment)
            }
            binding.tvItemCommentName.text = item.name
            binding.tvItemCommentEmail.text = item.email
            binding.tvItemComment.text = item.comment

            binding.tvItemCommentDelete.setOnClickListener {
                onClick.onClickDelete(item)
            }

            binding.tvItemCommentRe.setOnClickListener {
                onClick.onClick(item)
            }


            if (item.uid == CurrentUser.userData?.uid) {
                binding.tvItemCommentDelete.visibility = View.VISIBLE
            } else {
                binding.tvItemCommentDelete.visibility = View.INVISIBLE
            }

            if (item.reCommentData?.size == 0) {
                binding.tvItemCommentReComment.visibility = View.GONE
                binding.rvReComment.visibility = View.GONE
            } else {
                binding.tvItemCommentReComment.text = "댓글 ${item.reCommentData?.size}개"
                binding.tvItemCommentReComment.visibility = View.VISIBLE
                binding.rvReComment.visibility = View.GONE
                binding.tvItemCommentReClose.visibility = View.GONE
            }

            initRv(item.reCommentData)

            binding.tvItemCommentReComment.setOnClickListener {
                onClick.onClickList(item)
                binding.rvReComment.visibility = View.VISIBLE
                binding.tvItemCommentReComment.visibility = View.GONE
                binding.tvItemCommentReClose.visibility = View.VISIBLE
            }

            binding.tvItemCommentReClose.setOnClickListener {
                binding.tvItemCommentReClose.visibility = View.GONE
                binding.tvItemCommentReComment.visibility = View.VISIBLE
                binding.rvReComment.visibility = View.GONE
            }
        }

        private fun initRv(reCommentList: List<ReCommentDataModel>?) {
            val reCommentAdapter = ReCommentAdapter(object : ReCommentAdapter.ReCommentItemClick {
                override fun onClickEdit(item: ReCommentDataModel) {

                }

                override fun onClickDelete(item: ReCommentDataModel) {
                    onClick.onClickReCommentDelete(item)
                    initRv(reCommentList)
                }

            })
            with(binding.rvReComment) {
                adapter = reCommentAdapter
            }
            reCommentAdapter.submitList(reCommentList)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding =
            RvItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val commentList = getItem(position)
        holder.bind(commentList)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CommentDataModel>() {
            override fun areItemsTheSame(
                oldItem: CommentDataModel,
                newItem: CommentDataModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: CommentDataModel,
                newItem: CommentDataModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}