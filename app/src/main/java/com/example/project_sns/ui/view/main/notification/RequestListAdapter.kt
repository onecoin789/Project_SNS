package com.example.project_sns.ui.view.main.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.RvItemFollowBinding
import com.example.project_sns.databinding.RvItemProgressBlackBinding
import com.example.project_sns.ui.model.RequestModel

class RequestListAdapter(private val onItemClick: FollowItemClickListener) :
    ListAdapter<RequestModel, RecyclerView.ViewHolder>(diffUtil) {

    interface FollowItemClickListener {
        fun onClickAcceptButton(item: RequestModel)
        fun onClickRejectButton(item: RequestModel)
    }

    class RequestListViewHolder(
        private val binding: RvItemFollowBinding,
        private val onItemClick: FollowItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: RequestModel) {

            val userData = item.fromUid

            if (userData.profileImage != null) {
                binding.ivItemFollowProfile.clipToOutline = true
                Glide.with(binding.root).load(userData.profileImage).into(binding.ivItemFollowProfile)
            } else {
                Glide.with(binding.root).load(R.drawable.ic_user_fill).into(binding.ivItemFollowProfile)
            }

            binding.tvItemFollowName.text = userData.name
            binding.tvItemFollowEmail.text = userData.email

            binding.btnItemFollowAccept.setOnClickListener {
                onItemClick.onClickAcceptButton(item)
            }
            binding.btnItemFollowReject.setOnClickListener {
                onItemClick.onClickRejectButton(item)
            }
        }
    }

    class ProgressViewHolder(val binding: RvItemProgressBlackBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM) {
            val binding = RvItemFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return RequestListViewHolder(binding, onItemClick)
        } else {
            val binding = RvItemProgressBlackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ProgressViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RequestListViewHolder) {
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
        private const val ITEM = 0
        private const val LOADING = 1

        val diffUtil = object : DiffUtil.ItemCallback<RequestModel>() {
            override fun areItemsTheSame(oldItem: RequestModel, newItem: RequestModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: RequestModel,
                newItem: RequestModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}