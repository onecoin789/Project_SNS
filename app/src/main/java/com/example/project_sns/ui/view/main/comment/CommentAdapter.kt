package com.example.project_sns.ui.view.main.comment


import android.content.ClipData.Item
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemCommentBinding
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.view.model.CommentDataModel
import com.example.project_sns.ui.view.model.ReCommentDataModel

class CommentAdapter(private val onClick: ItemClick): ListAdapter<CommentDataModel, CommentAdapter.CommentViewHolder>(diffUtil) {

    interface ItemClick {
        fun onClick(item: CommentDataModel)
        fun onClickDelete(item: CommentDataModel)
    }


    class CommentViewHolder(
        private val binding: RvItemCommentBinding,
        private val onClick: ItemClick
    ): RecyclerView.ViewHolder(binding.root) {

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
                binding.rvReComment.visibility = View.VISIBLE
            }


            if (item.uid == CurrentUser.userData?.uid) {
                binding.tvItemCommentDelete.visibility = View.VISIBLE
            } else {
                binding.tvItemCommentDelete.visibility = View.INVISIBLE
            }
        }

        private fun initRv(commentData: List<ReCommentDataModel>?) {

          binding.rvReComment
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = RvItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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