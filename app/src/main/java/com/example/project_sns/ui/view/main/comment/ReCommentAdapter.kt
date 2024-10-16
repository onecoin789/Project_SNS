package com.example.project_sns.ui.view.main.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemProgressGrayBinding
import com.example.project_sns.databinding.RvItemReCommentBinding
import com.example.project_sns.ui.model.ReCommentModel

class ReCommentAdapter(private val onClick: ReCommentItemClick) :
    ListAdapter<ReCommentModel, RecyclerView.ViewHolder>(diffUtil) {

    interface ReCommentItemClick {
        fun onClickEdit(item: ReCommentModel)
        fun onClickDelete(item: ReCommentModel)
    }

    class ReCommentViewHolder(
        private val binding: RvItemReCommentBinding,
        private val onClick: ReCommentItemClick
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReCommentModel) {
            val userData = item.userData
            val reCommentData = item.reCommentData

            if (userData.profileImage != null) {
                binding.ivReComment.clipToOutline = true
                Glide.with(binding.root).load(userData.profileImage).into(binding.ivReComment)
            } else {
                Glide.with(binding.root).load(R.drawable.ic_user_fill).into(binding.ivReComment)
            }

            binding.tvItemReCommentName.text = userData.name
            binding.tvItemReCommentEmail.text = userData.email
            binding.tvItemReComment.text = reCommentData.comment

            binding.tvItemReCommentDelete.setOnClickListener {
                onClick.onClickDelete(item)
            }

            binding.tvItemReCommentEdit.setOnClickListener {
                onClick.onClickEdit(item)
            }
        }
    }

    class ProgressViewHolder(val binding: RvItemProgressGrayBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM) {
            val binding = RvItemReCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ReCommentViewHolder(binding, onClick)
        } else {
            val binding = RvItemProgressGrayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ProgressViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ReCommentViewHolder) {
            holder.bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            null -> LOADING
            else -> ITEM
        }
    }


    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ReCommentModel>() {
            override fun areItemsTheSame(
                oldItem: ReCommentModel,
                newItem: ReCommentModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ReCommentModel,
                newItem: ReCommentModel
            ): Boolean {
                return oldItem == newItem
            }
        }
        private const val ITEM = 0
        private const val LOADING = 1
    }
}