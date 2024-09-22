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
import com.example.project_sns.ui.mapper.toReCommentDataListEntity
import com.example.project_sns.ui.view.model.CommentModel
import com.example.project_sns.ui.view.model.ReCommentDataModel
import com.example.project_sns.ui.view.model.ReCommentModel

class CommentAdapter(private val onClick: CommentItemClick) :
    ListAdapter<CommentModel, CommentAdapter.CommentViewHolder>(diffUtil) {

    interface CommentItemClick {
        fun onClickCommentEdit(item: CommentModel)
        fun onClickCommentDelete(item: CommentModel)
        fun onClickReCommentList(item: CommentModel)
    }


    class CommentViewHolder(
        private val binding: RvItemCommentBinding,
        private val onClick: CommentItemClick
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommentModel) {

            val userData = item.userData
            val commentData = item.commentData

            if (userData.profileImage != null) {
                binding.ivComment.clipToOutline = true
                Glide.with(binding.root).load(userData.profileImage).into(binding.ivComment)
            } else {
                Glide.with(binding.root).load(R.drawable.ic_user_fill).into(binding.ivComment)
            }
            binding.tvItemCommentName.text = userData.name
            binding.tvItemCommentEmail.text = userData.email
            binding.tvItemComment.text = commentData.comment


            binding.tvItemCommentEdit.setOnClickListener {
                onClick.onClickCommentEdit(item)
            }

            binding.tvItemCommentDelete.setOnClickListener {
                onClick.onClickCommentDelete(item)
            }


            if (userData.uid == CurrentUser.userData?.uid) {
                binding.tvItemCommentDelete.visibility = View.VISIBLE
            } else {
                binding.tvItemCommentDelete.visibility = View.INVISIBLE
            }

            if (commentData.reCommentSize != 0) {
                binding.tvItemCommentReComment.text = "댓글 ${commentData.reCommentSize}개"
            } else {
                binding.tvItemCommentReComment.text = "댓글 남기기"
            }


            binding.tvItemCommentReComment.setOnClickListener {
                onClick.onClickReCommentList(item)
            }
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
        val diffUtil = object : DiffUtil.ItemCallback<CommentModel>() {
            override fun areItemsTheSame(
                oldItem: CommentModel,
                newItem: CommentModel
            ): Boolean {
                return oldItem.commentData == newItem.commentData
            }

            override fun areContentsTheSame(
                oldItem: CommentModel,
                newItem: CommentModel
            ): Boolean {
                return oldItem.commentData == newItem.commentData
            }

        }
    }
}