package com.example.project_sns.ui.view.main.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemReCommentBinding
import com.example.project_sns.ui.view.model.ReCommentDataModel

class ReCommentAdapter(private val onClick: ReCommentItemClick) :
    ListAdapter<ReCommentDataModel, ReCommentAdapter.ReCommentViewHolder>(diffUtil) {

    interface ReCommentItemClick {
        fun onClickEdit(item: ReCommentDataModel)
        fun onClickDelete(item: ReCommentDataModel)
    }

    class ReCommentViewHolder(
        private val binding: RvItemReCommentBinding,
        private val onClick: ReCommentItemClick
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReCommentDataModel) {
            if (item.profileImage != null) {
                binding.ivReComment.clipToOutline = true
                Glide.with(binding.root).load(item.profileImage).into(binding.ivReComment)
            } else {
                Glide.with(binding.root).load(R.drawable.ic_user_fill).into(binding.ivReComment)
            }

            binding.tvItemReCommentName.text = item.name
            binding.tvItemReCommentEmail.text = item.email
            binding.tvItemReComment.text = item.comment

            binding.tvItemReCommentDelete.setOnClickListener {
                onClick.onClickDelete(item)
            }

            binding.tvItemReCommentEdit.setOnClickListener {
                onClick.onClickEdit(item)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReCommentViewHolder {
        val binding = RvItemReCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReCommentViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ReCommentViewHolder, position: Int) {
        val reCommentList = getItem(position)
        holder.bind(reCommentList)
    }


    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ReCommentDataModel>() {
            override fun areItemsTheSame(
                oldItem: ReCommentDataModel,
                newItem: ReCommentDataModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ReCommentDataModel,
                newItem: ReCommentDataModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}